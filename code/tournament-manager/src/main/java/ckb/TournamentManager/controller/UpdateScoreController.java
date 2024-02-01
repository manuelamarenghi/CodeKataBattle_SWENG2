package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.UpdateScoreRequest;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/tournament/update-score")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UpdateScoreController extends Controller {
    private final TournamentService tournamentService;

    @PostMapping
    public ResponseEntity<Object> updateScore(@RequestBody UpdateScoreRequest request) {
        if (tournamentService.updateScore(request)) {
            return new ResponseEntity<>("Scores updated", getHeaders(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error occurred", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
