package ckb.BattleManager.service;

import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final BattleRepository battleRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, BattleRepository battleRepository) {
        this.teamRepository = teamRepository;
        this.battleRepository = battleRepository;
    }

    public List<Team> getListTeam(Long idBattle) {
        return teamRepository.findTeamsByBattle(
                battleRepository.findById(idBattle).orElseThrow(() -> new RuntimeException())
        );
    }

    public void createTeam(Long battleId, Long studentId) {
        teamRepository.save(new Team(studentId, battleId,"" , 0, false));
    }


}
