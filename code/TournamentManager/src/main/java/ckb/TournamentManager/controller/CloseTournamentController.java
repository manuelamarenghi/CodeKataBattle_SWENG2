package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.CloseTournamentRequest;
import ckb.TournamentManager.dto.GetTournamentPageRequest;
import ckb.TournamentManager.model.TournamentRanking;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/tournament/CloseTournament")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class CloseTournamentController extends Controller{
    private final TournamentService tournamentService;
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> CloseTournament(@RequestBody CloseTournamentRequest request) {
        // check if the request has valid data
        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;
        tournamentService.closeTournament(request.getTournamentID());
        return new ResponseEntity<>("Tournament closed", getHeaders(), HttpStatus.CREATED);
    }

    private ResponseEntity<Object> checkRequest(CloseTournamentRequest request) {
        if(request.getTournamentID() == null){
            log.error("Invalid tournament id request");
            return new ResponseEntity<>("Invalid tournament id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(tournamentService.getTournament(request.getTournamentID()) == null){
            log.error("Invalid tournament id request");
            return new ResponseEntity<>("Invalid tournament id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }

}
