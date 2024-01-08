package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.IdLong;
import ckb.BattleManager.dto.input.StudentBattle;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/battle")
@Slf4j
public class BattleController {
    private final BattleService battleService;

    @Autowired
    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping
    public ResponseEntity<Battle> getBattle(@RequestBody IdLong idBattle) {
        log.info("[API REQUEST] Get battle request with id: {}", idBattle.getId());

        try {
            return ResponseEntity.ok(battleService.getBattle(idBattle.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<List<Long>> getBattlesOfTournament(@RequestBody Long idTournament) {
        log.info("[API REQUEST] Get battles of tournament request with id: {}", idTournament);
        return ResponseEntity.ok(battleService.getBattleOfTournament(idTournament));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createBattle(@RequestBody Battle battle) {
        log.info("[API REQUEST] Create battle request with id: {}", battle.getBattleId());
        log.debug("Battle: {}", battle);
        battleService.createBattle(battle);
    }

    public ResponseEntity<Object> joinBattle(@RequestBody StudentBattle request) {
        log.info("[API REQUEST] Join battle request with id_battle: {}, id_student: {}", request.getIdBattle(), request.getIdStudent());
        try {
            battleService.joinBattle(request.getIdStudent(), request.getIdBattle());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Object> leaveBattle(@RequestBody StudentBattle request) {
        log.info("[API REQUEST] Leave battle request with id_battle: {}, id_student: {}", request.getIdBattle(), request.getIdStudent());
        battleService.leaveBattle(request.getIdStudent(), request.getIdBattle());
        return ResponseEntity.ok().build();
    }

}
