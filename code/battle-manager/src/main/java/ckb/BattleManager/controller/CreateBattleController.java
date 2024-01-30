package ckb.BattleManager.controller;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battle")
@Slf4j
public class CreateBattleController {
    private final BattleService battleService;

    @Autowired
    public CreateBattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("/create-battle")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createBattle(@RequestBody Battle battle) {
        log.info("[API REQUEST] Create battle request with id: {}", battle.getBattleId());
        log.debug("Battle: {}", battle);
        try {
            // TODO have a dto with manu and make some tests on the battle
            battleService.createBattle(battle);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
