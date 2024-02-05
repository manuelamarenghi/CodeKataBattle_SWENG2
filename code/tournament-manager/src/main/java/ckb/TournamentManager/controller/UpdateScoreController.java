package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.in.UpdateScoreRequest;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/tournament/update-score")
@RequiredArgsConstructor
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
