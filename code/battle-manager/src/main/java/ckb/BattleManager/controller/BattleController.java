package ckb.BattleManager.controller;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battle")
public class BattleController {
    private final BattleService battleService;

    @Autowired
    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping
    public ResponseEntity<Battle> getBattle(Long id) {
        return ResponseEntity.ok(battleService.getBattle(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createBattle(@RequestBody Battle battle) {
        battleService.addNewBattle(battle);
    }


    public void joinBattle(Long battleId, Long studentId) {
        battleService.joinBattle(battleId, studentId);
    }

    public void leaveBattle() {

    }
}
