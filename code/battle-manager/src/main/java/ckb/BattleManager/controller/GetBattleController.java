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

    /**
     * Method to get a battle
     *
     * @param idBattle id of the battle
     * @return a ResponseEntity with the battle or a not found status
     */
    @GetMapping("/getBattle")
    public ResponseEntity<Battle> getBattle(@RequestBody IdLong idBattle) {
        log.info("[API REQUEST] Get battle request with id: {}", idBattle.getId());

        try {
            // TODO: dto with manu?
            return ResponseEntity.ok(battleService.getBattle(idBattle.getId()));
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Method to get all the battles of a tournament
     * ATTENTION: I cannot control the id of the tournament
     *
     * @param idTournament id of the tournament
     * @return a ResponseEntity with the list of ids of the battles
     */
    @GetMapping("/getBattlesOfTournament")
    public ResponseEntity<List<Long>> getBattlesOfTournament(@RequestBody IdLong idTournament) {
        log.info("[API REQUEST] Get battles of tournament request with id: {}", idTournament.getId());
        return ResponseEntity.ok(battleService.getBattleOfTournament(idTournament.getId()));
    }
}
