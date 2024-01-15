package ckb.BattleManager.service;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final BattleService battleService;
    private final ParticipationService participationService;

    @Autowired
    public TeamService(TeamRepository teamRepository, @Lazy BattleService battleService, ParticipationService participationService) {
        this.teamRepository = teamRepository;
        this.battleService = battleService;
        this.participationService = participationService;
    }

    public Team getTeam(Long idTeam) throws Exception {
        return teamRepository.findById(idTeam).orElseThrow(() -> {
            log.info("Team not found with id: {}", idTeam);
            return new Exception();
        });
    }

    public List<Team> getListTeam(Long idBattle) throws Exception {
        return teamRepository.findTeamsByBattle(
                battleService.findBattleById(idBattle).orElseThrow(() -> {
                    log.info("Battle not found with id: {}", idBattle);
                    return new Exception();
                })
        );
    }

    public void createTeam(Long studentId, Long battleId) throws Exception {
        Team team = new Team(studentId,
                battleService.findBattleById(battleId).orElseThrow(() -> {
                    log.info("Battle not found with id: {}", battleId);
                    return new Exception("");
                }),
                "" ,
                0,
                false
        );

        teamRepository.save(team);
        participationService.createParticipation(studentId, team);
    }

    public void deleteParticipation(Long idStudent, Long idBattle) throws Exception {
        participationService.deleteParticipationHavingIdBattle(idStudent, idBattle);
    }

    public void assignScore(Long idTeam, Integer score) throws Exception {
        Optional<Team> optionalTeam = teamRepository.findById(idTeam);

        if (optionalTeam.isPresent()) {
            Team team = optionalTeam.get();
            team.setScore(score);
            teamRepository.save(team);
            log.info("Team score updated with id {} and score: {}", idTeam, score);
        } else {
            log.info("Team not found with id: {}", idTeam);
            throw new Exception();
        }
    }

    public void assignPersonalScore(Long idTeam, Integer score) throws Exception {
        Optional<Team> optionalTeam = teamRepository.findById(idTeam);

        if (optionalTeam.isPresent()) {
            Team team = optionalTeam.get();
            team.setScore(team.getScore() + score);
            team.setEduEvaluated(true);
            teamRepository.save(team);
            log.info("Team personal score updated with id {} and score: {}", idTeam, score);
        } else {
            log.info("Team not found with id: {}", idTeam);
            throw new Exception();
        }
    }

    public void deleteTeam(Long teamId) {
        teamRepository.deleteById(teamId);
        log.info("Team deleted with id: {}", teamId);
    }

    public void registerStudentToTeam(Long idStudent, Long idNewTeam) throws Exception {
        // Get the battle in which the student is enrolled
        Optional<Battle> optionalBattleRegistered = teamRepository.findBattleByTeamId(idNewTeam);
        Battle battleRegistered = optionalBattleRegistered.orElseThrow(
                () -> new Exception("The team " + idNewTeam + " is not subscribed to any battle")
        );

        // retrieve the team of the student in the same battle
        Optional<Team> optionalTeam = teamRepository.findTeamByBattleAndParticipationId_TeamId(
                battleRegistered, idStudent
        );

        // delete the participation of the student in the team
        optionalTeam.ifPresent(
                team -> participationService.deleteParticipationHavingIdTeam(idStudent, team)
        );

        // create a new participation
        participationService.createParticipation(idStudent,
                teamRepository.findById(idNewTeam).orElseThrow(
                        () -> new Exception("Team not found with id: " + idNewTeam)
                )
        );
        log.info("Student {} registered to team {}", idStudent, idNewTeam);
    }

    public List<Pair<Long, Long>> getListPairIdUserPoints(Battle battle) {
        return teamRepository.findPairsIdUserPointsByBattleId(battle);
    }
}
