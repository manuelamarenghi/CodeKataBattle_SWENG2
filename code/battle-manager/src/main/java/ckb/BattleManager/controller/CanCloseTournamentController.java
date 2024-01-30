package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.CloseTournamentRequest;
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
public class CanCloseTournamentController {
    private final BattleService battleService;

    @Autowired
    public CanCloseTournamentController(BattleService battleService) {
        this.battleService = battleService;
    }

    /**
     * Method used to check if the battles of a tournament are finished
     * in order to determine if a tournament can be closed
     * ATTENTION: I cannot control the id of the tournament
     *
     * @param request id of the tournament
     * @return a ResponseEntity with a true if the tournament can be close, false otherwise
     */
    @PostMapping("/battles-finished")
    public ResponseEntity<Boolean> canCloseTournament(@RequestBody CloseTournamentRequest request) {
        log.info("[API REQUEST] Battle finished request with id tournament: {}", request.getIdTournament());

        return ResponseEntity.ok(battleService.canCloseTournament(request.getIdTournament()));
    }
}
