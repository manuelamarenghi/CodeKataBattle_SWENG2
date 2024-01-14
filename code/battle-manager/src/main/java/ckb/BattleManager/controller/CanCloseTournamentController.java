package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.IdLong;
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

    @PostMapping("/battleFinished")

    public ResponseEntity<Boolean> canCloseTournament(@RequestBody IdLong idTournament) {
        log.info("[API REQUEST] Battle finished request with id tournament: {}", idTournament.getId());

        return ResponseEntity.ok(battleService.canCloseTournament(idTournament.getId()));
    }
}
