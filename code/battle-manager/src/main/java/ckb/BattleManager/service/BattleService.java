package ckb.BattleManager.service;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.repository.BattleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BattleService {
    private final BattleRepository battleRepository;
    private final TeamService teamService;

    @Autowired
    public BattleService(BattleRepository battleRepository, TeamService teamService) {
        this.battleRepository = battleRepository;
        this.teamService = teamService;
    }

    public Battle getBattle(Long id) {
        return battleRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
    }

    public void addNewBattle(Battle battle) {
        battleRepository.save(battle);
    }

    public void joinBattle(Long battleId, Long studentId) {
        teamService.createTeam(battleId, studentId);
    }
}
