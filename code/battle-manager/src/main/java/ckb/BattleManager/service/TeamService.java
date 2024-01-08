package ckb.BattleManager.service;

import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.TeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public TeamService(TeamRepository teamRepository, BattleService battleService, ParticipationService participationService) {
        this.teamRepository = teamRepository;
        this.battleService = battleService;
        this.participationService = participationService;
    }

    public Team getTeam(Long idTeam) throws Exception {
        return teamRepository.findById(idTeam).orElseThrow(() -> {
            log.info("Team not found with id: {}", idTeam);
            return new Exception("");
        });
    }

    public List<Team> getListTeam(Long idBattle) throws Exception {
        return teamRepository.findTeamsByBattle(
                battleService.findBattleById(idBattle).orElseThrow(() -> {
                    log.info("Battle not found with id: {}", idBattle);
                    return new Exception("");
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
        //TODO voglio che quando non ci sono più membri nel team, venga cancellato
        participationService.deleteParticipation(idStudent, idBattle);
    }

    public void assignScore(Long idTeam, Integer score) throws Exception {
        Optional<Team> team = teamRepository.findById(idTeam);

        if (team.isPresent()) {
            team.get().setEduEvaluated(true);
            team.get().setScore(score);
            teamRepository.save(team.get());
        } else {
            log.info("Team not found with id: {}", idTeam);
            throw new Exception("");
        }
    }
}
