package ckb.BattleManager.service;

import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
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

    public void deleteParticipation(Long idStudent, Long idBattle) {
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

    public void registerStudentToTeam(Long idStudent, Long idTeam) {
        // delete the record in participation and if the team has 0 members
        // delete the team, also insert a new line in participation
        participationService.deleteParticipationHavingIdTeam(idStudent,
                teamRepository.getReferenceById(idTeam));
        
    }

    public void inviteStudentToTeam(Long idStudent, Long idTeam) {
        //TODO
    }
}
