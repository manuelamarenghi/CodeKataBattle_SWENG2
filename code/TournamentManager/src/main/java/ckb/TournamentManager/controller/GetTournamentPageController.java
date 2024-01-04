package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.GetTournamentPageRequest;
import ckb.TournamentManager.model.TournamentRanking;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/tournament/GetTournamentPage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class GetTournamentPageController extends Controller{
    private final TournamentService tournamentService;

    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> getTournamentPage(@RequestBody GetTournamentPageRequest request) {
        // check if the request has valid data
        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;
        List<TournamentRanking> t = tournamentService.getTournamentPage(request);
        // mandare richiesta a battle service per le battle relative al tournament
        ResponseWrapper responseWrapper;
        try {
            log.info("Tournament page retrieved");
            List<Long> battles = getBattles(request.getTournamentID());
            responseWrapper = new ResponseWrapper(battles, t);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>("Error while retrieving battles", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseWrapper, getHeaders(), HttpStatus.CREATED);
    }
    private List<Long> getBattles(Long variableValue) {
        try {
            return webClient.get()
                    .uri("http://localhost:8082/api/battle/nome", uribuilder -> uribuilder
                            .queryParam("tournamentID", variableValue)
                            .build())
                    .retrieve()
                    .bodyToFlux(Long.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            // Gestire l'eccezione solo se il codice di stato non è 200 OK
            if (e.getStatusCode() != HttpStatus.OK) {
                log.error("Error while retrieving battles: {}", e.getMessage());
                return Collections.emptyList();
            }
            // Puoi gestire l'errore restituendo una lista vuota o un altro valore di default
            return Collections.emptyList();
        }
    }
    private ResponseEntity<Object> checkRequest(GetTournamentPageRequest request) {
        if(request.getTournamentID() == null || tournamentService.getTournament(request.getTournamentID()) == null){
            log.error("Invalid tournament id request");
            return new ResponseEntity<>("Invalid tournament id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }


}
