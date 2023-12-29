package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.NewTournamentRequest;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/api/tournament/new-tournament")
@RequiredArgsConstructor

public class NewTournamentController extends Controller{
     private final TournamentService tournamentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> newTournament(@RequestBody NewTournamentRequest request) {
        // check if the request has valid data
        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;

        String content = " Hi! A new tournament is created click here to subscribe "+tournamentService.createTournament(request);
        log.info("New tournament created");
        // mandare mail a tutti gli utenti registrati


        return new ResponseEntity<>("Tournament created", getHeaders(), HttpStatus.CREATED);
    }

    private ResponseEntity<Object> checkRequest(NewTournamentRequest request) {
        if (request.getReg_deadline() == null) {
            log.error("Invalid request");
            return new ResponseEntity<>("Invalid data request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(request.getReg_deadline().before(new java.util.Date())){
            log.error("Invalid request");
            return new ResponseEntity<>("Invalid data request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }


}
