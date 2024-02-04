package ckb.BattleManager.service;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.model.WorkingPair;
import ckb.BattleManager.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class TeamService {
    private final int MAX_TIME_DEDUCTION = 50;
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
        Team team = Team.builder()
                .battle(battle)
                .eduEvaluated(false)
                .score(0)
                .canParticipateToBattle(true)
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

            studentTeam.setCanParticipateToBattle(false);
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

        if (team.getCanParticipateToBattle().equals(false)) {
            log.error("Team score cannot be updated because the team cannot participate");
            throw new Exception("Team score cannot be updated because the team cannot participate");
        }

        if (battleOfTeam.getSubDeadline().isBefore(LocalDateTime.now())) {
            log.error("Team score cannot be updated because the tournament is closed");
            throw new Exception("Team score cannot be updated because the tournament is closed");
        }

        int timeDeduction = computeTimeDeduction(battleOfTeam);
        log.info("Time deduction for team with id {}: {}", idTeam, timeDeduction);
        score = score - timeDeduction;

        team.setScore(Math.max(score, team.getScore()));
        teamRepository.save(team);

        log.info("Team score updated with id {} and score: {}", idTeam, score);
    }

    private Integer computeTimeDeduction(Battle battleOfTeam) {
        LocalDateTime subDeadline = battleOfTeam.getSubDeadline();
        LocalDateTime registrationDeadline = battleOfTeam.getRegDeadline();
        LocalDateTime now = LocalDateTime.now();

        long battleMinutes = ChronoUnit.MINUTES.between(registrationDeadline, subDeadline);
        long minutesPassed = ChronoUnit.MINUTES.between(registrationDeadline, now);

        return MAX_TIME_DEDUCTION *( (int) (minutesPassed / battleMinutes));
    }

    public void assignPersonalScore(Long idTeam, Integer score, Long idEducator) throws Exception {
        if (score < 0) {
            log.info("Score cannot be negative");
            throw new Exception("Score cannot be negative");
        }

        if (score > 10) {
            log.info("Score cannot be greater than 10");
            throw new Exception("Score cannot be greater than 10");
        }

        Team team = getTeam(idTeam);
        Battle battleOfTeam = team.getBattle();

        if (battleOfTeam.getIsClosed()) {
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

        team.setScore(Math.min(team.getScore() + score, 100));
        team.setEduEvaluated(true);
        teamRepository.save(team);

        log.info("Score of the team with id {} has been updated, score: {}", idTeam, score);
    }

    public void registerStudentToTeam(Long idStudent, Long idNewTeam) throws Exception {
        // Get the battle in which the student is enrolled
        Team teamToSubscribe = getTeam(idNewTeam);
        Battle battleRegistered = teamToSubscribe.getBattle();

        // Check if the student is registered to the battle
        if (teamRepository.findTeamByStudentIdAndBattle(idStudent, battleRegistered).isEmpty()) {
            // create a new participation
            participationService.createParticipation(
                    idStudent,
                    teamToSubscribe
            );
        } else{
            deleteParticipation(idStudent, battleRegistered);
            // create a new participation
            participationService.createParticipation(
                    idStudent,
                    teamToSubscribe
            );
        }

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
