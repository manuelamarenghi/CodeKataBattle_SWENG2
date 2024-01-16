package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.CloseTournamentRequest;
import ckb.TournamentManager.dto.outcoming.AbleToCloseTRequest;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

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
        if(contactBattleManager(request)){
            if(tournamentService.closeTournament(request)){
                return new ResponseEntity<>("Tournament closed", getHeaders(), HttpStatus.CREATED);
            }
            else return new ResponseEntity<>("Not allowed to close tournament", getHeaders(), HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>("Not possible to close", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean contactBattleManager(CloseTournamentRequest request) {
        try {
            String responseString = webClient
                    .post()
                    .uri("http://localhost:8082/api/battle/servizio")
                    .bodyValue(new AbleToCloseTRequest(request.getTournamentID()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return Boolean.parseBoolean(responseString);
        } catch (Exception e) {
            log.error("Error while retrieving battles");
            return false;
        }
    }

    private ResponseEntity<Object> checkRequest(CloseTournamentRequest request) {
        if(request.getTournamentID() == null || request.getCreatorID() == null){
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
