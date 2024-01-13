package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.StudentBattle;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/battle")
@Slf4j
public class JoinLeaveBattleController {
    private final BattleService battleService;

    @Autowired
    public JoinLeaveBattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("/joinBattle")
    public ResponseEntity<Object> joinBattle(@RequestBody StudentBattle request) {
        log.info("[API REQUEST] Join battle request with id_battle: {}, id_student: {}", request.getIdBattle(), request.getIdStudent());
        try {
            battleService.joinBattle(request.getIdStudent(), request.getIdBattle());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/leaveBattle")
    public ResponseEntity<Object> leaveBattle(@RequestBody StudentBattle request) {
        log.info("[API REQUEST] Leave battle request with id_battle: {}, id_student: {}", request.getIdBattle(), request.getIdStudent());
        try {
            battleService.leaveBattle(request.getIdStudent(), request.getIdBattle());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
