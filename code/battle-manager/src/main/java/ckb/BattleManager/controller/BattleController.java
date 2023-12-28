package ckb.BattleManager.controller;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battle/get")
public class BattleController {
    private final BattleService battleService;

    @Autowired
    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Battle getBattle(Long id) {
        return battleService.getBattle(id);
    }

    @PostMapping
    public void registerNewBattle(@RequestBody Battle battle) {
        battleService.addNewBattle(battle);
    }
}
