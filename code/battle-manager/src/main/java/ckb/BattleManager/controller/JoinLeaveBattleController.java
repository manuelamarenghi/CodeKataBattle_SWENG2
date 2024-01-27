package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.StudentBattle;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    /**
     * Method to join a battle
     *
     * @param request a pair of idStudent and idBattle
     * @return a ResponseEntity with ok status or a bad request
     */
    @PostMapping("/joinBattle")
    public ResponseEntity<Object> joinBattle(@RequestBody StudentBattle request) {
        log.info("[API REQUEST] Join battle request with id_battle: {}, id_student: {}", request.getIdBattle(), request.getIdStudent());
        try {
            battleService.joinBattle(request.getIdStudent(), request.getIdBattle());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Method to leave a battle
     *
     * @param request a pair of idStudent and idBattle
     * @return a ResponseEntity with ok status
     */
    @PostMapping("/leaveBattle")
    public ResponseEntity<Object> leaveBattle(@RequestBody StudentBattle request) {
        log.info("[API REQUEST] Leave battle request with id_battle: {}, id_student: {}", request.getIdBattle(), request.getIdStudent());
        try {
            battleService.leaveBattle(request.getIdStudent(), request.getIdBattle());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
