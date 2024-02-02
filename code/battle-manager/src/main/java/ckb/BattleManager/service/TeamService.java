package ckb.BattleManager.service;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.model.WorkingPair;
import ckb.BattleManager.repository.ParticipationRepository;
import ckb.BattleManager.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final ParticipationRepository participationRepository;
    private final ParticipationService participationService;

    @Autowired
    public TeamService(TeamRepository teamRepository, ParticipationRepository participationRepository, ParticipationService participationService) {
        this.teamRepository = teamRepository;
        this.participationService = participationService;
        this.participationRepository = participationRepository;
    }

    public Team getTeam(Long idTeam) throws Exception {
        return teamRepository.findById(idTeam).orElseThrow(() -> {
            log.info("Team not found with id: {}", idTeam);
            return new Exception("Team not found with id: " + idTeam);
        });
    }

    public Team getTeam(Battle battle, Long idStudent) throws Exception {
        return teamRepository.findTeamByStudentIdAndBattle(idStudent, battle).orElseThrow(
                () -> {
                    log.info("Team not found with id student {} and battle {}", idStudent, battle.getName());
                    return new Exception("Team not found with id student " + idStudent +
                            " and battle: " + battle.getName());
                }
        );
    }

    public void createTeam(Long studentId, Battle battle) {
        // TODO: how to set the repository link?
        Team team = Team.builder()
                .battle(battle)
                .repositoryLink("")
                .eduEvaluated(false)
                .score(0)
                .isEmpty(false)
                .build();

        Participation participation = new Participation();
        participation.setTeam(team);
        participation.setStudentId(studentId);
        team.setParticipation(
                List.of(
                        participation
                )
        );
        teamRepository.save(team);

        log.info("Team created with id {} and participation {}", team.getTeamId(), team.getParticipation());
    }

    private Team getTeamByStudentIdAndBattle(Long studentId, Battle battle) throws Exception {
        return teamRepository.findTeamByStudentIdAndBattle(studentId, battle).orElseThrow(
                () -> {
                    log.error("Team not found with id student {} and battle {}", studentId, battle.getName());
                    return new Exception("Team not found with id student " + studentId +
                            " and battle: " + battle.getName());
                }
        );
    }

    @Transactional
    public void deleteParticipation(Long idStudent, Battle battle) throws Exception {
        Team studentTeam = getTeamByStudentIdAndBattle(idStudent, battle);

        if (studentTeam.getParticipation().size() == 1) {
            Participation participation = studentTeam.getParticipation().getFirst();
            studentTeam.getParticipation().remove(participation);

            //participationRepository.deleteAll(studentTeam.getParticipation());
            //participationService.deleteParticipationById(idStudent, studentTeam);
            //participationService.deleteParticipationById(participation.getId());
            //teamRepository.deleteById(studentTeam.getTeamId());
            studentTeam.setIsEmpty(true);
            log.info("Team deleted with id: {}", studentTeam.getTeamId());
        } else {
            participationService.deleteParticipationById(idStudent, studentTeam);
        }
    }

    public void assignScore(Long idTeam, Integer score) throws Exception {
        if (score < 0) {
            log.info("Score cannot be negative");
            throw new Exception("Score cannot be negative");
        }

        if (score > 100) {
            log.info("Score cannot be greater than 100");
            throw new Exception("Score cannot be greater than 100");
        }

        Team team = getTeam(idTeam);
        Battle battleOfTeam = team.getBattle();

        if (battleOfTeam.getSubDeadline().isBefore(LocalDateTime.now())) {
            log.error("Team score cannot be updated because the tournament is closed");
            throw new Exception("Team score cannot be updated because the tournament is closed");
        }

        team.setScore(Math.max(score, team.getScore()));
        teamRepository.save(team);

        log.info("Team score updated with id {} and score: {}", idTeam, score);
    }

    public void assignPersonalScore(Long idTeam, Integer score, Long idEducator) throws Exception {
        if (score < 0) {
            log.info("Score cannot be negative");
            throw new Exception("Score cannot be negative");
        }

        if (score > 100) {
            log.info("Score cannot be greater than 100");
            throw new Exception("Score cannot be greater than 100");
        }

        Team team = getTeam(idTeam);
        Battle battleOfTeam = team.getBattle();

        if (battleOfTeam.getHasEnded()) {
            log.error("Team score cannot be updated because the battle is closed");
            throw new Exception("Team score cannot be updated because the battle is closed");
        }

        if (!battleOfTeam.getBattleToEval()) {
            log.error("Team personal score cannot be updated because the battle can not be evaluated by an educator");
            throw new Exception("Team personal score cannot be updated because the battle can not be evaluated by an educator");
        }

        if (!battleOfTeam.getAuthorId().equals(idEducator)) {
            log.error("Team personal score cannot be updated because the educator is not the author of the battle");
            throw new Exception("Team personal score cannot be updated because the educator is not the author of the battle");
        }

        if (team.getEduEvaluated()) {
            log.error("Team personal score cannot be updated because the team has already been evaluated");
            throw new Exception("Team personal score cannot be updated because the team has already been evaluated");
        }

        team.setScore(team.getScore() + score);
        team.setEduEvaluated(true);
        teamRepository.save(team);

        log.info("Team personal score updated with id {} and score: {}", idTeam, score);
    }

    public void registerStudentToTeam(Long idStudent, Long idNewTeam) throws Exception {
        // Get the battle in which the student is enrolled
        Team teamToSubscribe = getTeam(idNewTeam);
        Battle battleRegistered = teamToSubscribe.getBattle();

        // Check if the student is registered to the battle
        if (teamRepository.findTeamByStudentIdAndBattle(idStudent, battleRegistered).isEmpty()) {
            log.error("Student {} is not registered to the battle {}", idStudent, battleRegistered.getName());
            throw new Exception("Student " + idStudent + " is not registered to the battle " + battleRegistered.getName());
        }

        deleteParticipation(idStudent, battleRegistered);

        // create a new participation
        participationService.createParticipation(
                idStudent,
                teamToSubscribe
        );

        log.info("Student {} registered to team {}", idStudent, idNewTeam);
    }

    public List<WorkingPair<Long, Integer>> getListPairIdUserPoints(Battle battle) {
        List<Object[]> listArrayObjects = teamRepository.findPairsIdUserPointsByBattleId(battle);
        return listArrayObjects.stream()
                .map(arrayObject -> new WorkingPair<>((Long) arrayObject[0], (Integer) arrayObject[1]))
                .toList();
    }

    public List<Long> getBattleParticipants(Battle battle) {
        return teamRepository.findTeamsByBattle(battle).stream()
                .map(Team::getParticipation)
                .flatMap(Collection::stream)
                .map(Participation::getStudentId)
                .toList();
    }
}
