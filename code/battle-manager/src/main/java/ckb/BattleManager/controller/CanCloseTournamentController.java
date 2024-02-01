package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.CloseTournamentRequest;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battle")
@Slf4j
@CrossOrigin(origins = "*")
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
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> canCloseTournament(@RequestBody CloseTournamentRequest request) {
        log.info("[API REQUEST] Battle finished request with id tournament: {}", request.getTournamentID());
        if (battleService.canCloseTournament(request.getTournamentID())) {
            return new ResponseEntity<>(true, getHeaders(), HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body(false);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return headers;
    }
}
