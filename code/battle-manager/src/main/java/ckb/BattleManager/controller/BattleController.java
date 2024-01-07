package ckb.BattleManager.controller;

import ckb.BattleManager.dto.IdLong;
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
        return ResponseEntity.ok(battleService.getBattle(idBattle.getId()));
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


    //TODO: not very convinto because there are two services to use participations and team
    public void joinBattle(@RequestBody Long idBattle, @RequestBody Long idStudent) {
        log.info("[API REQUEST] Join battle request with id_battle: {}, id_student: {}", idBattle, idStudent);
        battleService.joinBattle(idBattle, idStudent);
    }

    public void leaveBattle() {

    }
}
