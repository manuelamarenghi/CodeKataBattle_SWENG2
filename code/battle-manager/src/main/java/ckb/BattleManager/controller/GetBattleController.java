package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.GetBattlesRequest;
import ckb.BattleManager.dto.output.ListBattlesResponse;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Method to get all the battles of a tournament
     * ATTENTION: I cannot control the id of the tournament
     *
     * @param idTournament id of the tournament
     * @return a ResponseEntity with the list of ids of the battles
     */
    @GetMapping("/get-battles-tournament")
    public ResponseEntity<ListBattlesResponse> getBattlesOfTournament(@RequestBody GetBattlesRequest idTournament) {
        log.info("[API REQUEST] Get battles of tournament request with id: {}", idTournament.getBattleId());
        List<Long> battleIds = battleService.getBattlesTournament(idTournament.getBattleId());
        log.info("The battles of the tournament {} are: {}", idTournament.getBattleId(), battleIds);
        return ResponseEntity.ok(new ListBattlesResponse(battleIds));
    }
}
