package ckb.BattleManager.service;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.repository.BattleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BattleService {
    private final BattleRepository battleRepository;

    @Autowired
    public BattleService(BattleRepository battleRepository) {
        this.battleRepository = battleRepository;
    }

    public Battle getBattle(Long id) {
        return battleRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
    }

    public void addNewBattle(Battle battle) {

    }
}
