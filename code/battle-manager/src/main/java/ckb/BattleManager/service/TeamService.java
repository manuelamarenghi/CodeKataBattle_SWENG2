package ckb.BattleManager.service;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final ParticipationService participationService;

    @Autowired
    public TeamService(TeamRepository teamRepository, ParticipationService participationService) {
        this.teamRepository = teamRepository;
        this.participationService = participationService;
    }

    public Team getTeam(Long idTeam) throws Exception {
        return teamRepository.findById(idTeam).orElseThrow(() -> {
            log.info("Team not found with id: {}", idTeam);
            return new Exception("Team not found with id: " + idTeam);
        });
    }

    public void createTeam(Long studentId, Battle battle) {
        Team team = new Team();
        team.setBattle(battle);
        team.setParticipation(null);
        // TODO: how to set the repository link?
        team.setRepositoryLink("");
        team.setEduEvaluated(false);
        team.setScore(0);
        team.setParticipation(List.of(
                new Participation(
                        new ParticipationId(
                                studentId,
                                team
                        )
                )
        ));

        teamRepository.save(team);
    }

    public void deleteParticipation(Long idStudent, Battle battle) throws Exception {
        Team studentTeam = teamRepository.findTeamByStudentIdAndBattle(idStudent, battle).orElseThrow(
                () -> {
                    log.info("Team not found with id student {} and id battle {}", idStudent, battle.getBattleId());
                    return new Exception("Team not found with id student " + idStudent +
                            " and id battle: " + battle.getBattleId());
                }
        );
        participationService.deleteParticipationHavingIdBattle(idStudent, studentTeam);

        if (studentTeam.getParticipation().isEmpty()) {
            teamRepository.delete(studentTeam);
            log.info("Team deleted with id: {}", studentTeam.getTeamId());
        }
    }

    public void assignScore(Long idTeam, Integer score) throws Exception {
        Team team = checkScoreAndRetrieveTeam(idTeam, score);

        Battle battleOfTeam = team.getBattle();

        if (battleOfTeam.getSubDeadline().isBefore(LocalDateTime.now())) {
            log.info("Team score cannot be updated because the tournament is closed");
            throw new Exception("Team score cannot be updated because the tournament is closed");
        }

        team.setScore(score);
        teamRepository.save(team);
        log.info("Team score updated with id {} and score: {}", idTeam, score);
    }

    public void assignPersonalScore(Long idTeam, Integer score) throws Exception {
        Team team = checkScoreAndRetrieveTeam(idTeam, score);

        Battle battleOfTeam = team.getBattle();
        if (battleOfTeam.getHasEnded()) {
            log.info("Team score cannot be updated because the battle is closed");
            throw new Exception("Team score cannot be updated because the battle is closed");
        }

        team.setScore(team.getScore() + score);
        team.setEduEvaluated(true);
        teamRepository.save(team);
        log.info("Team personal score updated with id {} and score: {}", idTeam, score);

    }

    private Team checkScoreAndRetrieveTeam(Long idTeam, Integer score) throws Exception {
        if (score < 0) {
            log.info("Score cannot be negative");
            throw new Exception("Score cannot be negative");
        }

        return teamRepository.findById(idTeam).orElseThrow(
                () -> {
                    log.info("Team not found with id: {}", idTeam);
                    return new Exception("Team not found with id: " + idTeam);
                }
        );
    }

    public void registerStudentToTeam(Long idStudent, Long idNewTeam) throws Exception {
        // Check if the student is registered to the battle

        // Get the battle in which the student is enrolled
        Team teamToSubscribe = teamRepository.findById(idNewTeam).orElseThrow(
                () -> {
                    log.info("Team not found with id: {}", idNewTeam);
                    return new Exception("Team not found with id: " + idNewTeam);
                }
        );

        Battle battleRegistered = teamToSubscribe.getBattle();

        // retrieve the team of the student in the same battle
        Team oldTeam = teamRepository.findTeamByStudentIdAndBattle(
                idStudent, battleRegistered
        ).orElseThrow(
                () -> {
                    log.info("Team not found with id student {} and id battle {}", idStudent, battleRegistered.getBattleId());
                    return new Exception("Team not found with id student " + idStudent +
                            " and id battle: " + battleRegistered.getBattleId());
                }
        );

        // delete the participation of the student in the team
        participationService.deleteParticipationHavingIdTeam(idStudent, oldTeam);

        if (oldTeam.getParticipation().isEmpty()) {
            teamRepository.delete(oldTeam);
            log.info("Team deleted with id: {}", oldTeam.getTeamId());
        }

        // create a new participation
        participationService.createParticipation(
                idStudent,
                teamToSubscribe
        );

        log.info("Student {} registered to team {}", idStudent, idNewTeam);
    }

    public List<Pair<Long, Integer>> getListPairIdUserPoints(Battle battle) {
        List<Object[]> listArrayObjects = teamRepository.findPairsIdUserPointsByBattleId(battle);
        return listArrayObjects.stream()
                .map(arrayObject -> Pair.of((Long) arrayObject[0], (Integer) arrayObject[1]))
                .toList();
    }
}
