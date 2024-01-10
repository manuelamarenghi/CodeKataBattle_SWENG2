package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.IdLong;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/battle")
public class GetBattleController {
    private final BattleService battleService;

    @Autowired
    public GetBattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping("/getBattle")
    public ResponseEntity<Battle> getBattle(@RequestBody IdLong idBattle) {
        log.info("[API REQUEST] Get battle request with id: {}", idBattle.getId());

        try {
            return ResponseEntity.ok(battleService.getBattle(idBattle.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/getBattlesOfTournament")
    public ResponseEntity<List<Long>> getBattlesOfTournament(@RequestBody IdLong idTournament) {
        log.info("[API REQUEST] Get battles of tournament request with id: {}", idTournament.getId());
        return ResponseEntity.ok(battleService.getBattleOfTournament(idTournament.getId()));
    }
}
