package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.CloseTournamentRequest;
import ckb.TournamentManager.dto.incoming.UpdateScoreRequest;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@Slf4j
@RequestMapping("/api/tournament/UpdateScore")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class UpdateScoreController extends Controller{
    private final TournamentService tournamentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> UpdateScore(@RequestBody UpdateScoreRequest request) {
        if(tournamentService.updateScore(request)){
        return new ResponseEntity<>("Scores updated", getHeaders(), HttpStatus.CREATED);
        }else{
        return new ResponseEntity<>("Error occurred", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }}
}
