package ckb.TournamentManager.controller;

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
@RequestMapping("/api/tournament/GetTournamentPage")
@RequiredArgsConstructor

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
        log.info("Tournament page retrieved");
        // mandare richiesta a battle service per le battle relative al tournament
        List<Long> battles = getBattles(request.getTournamentId());
        ResponseWrapper responseWrapper = new ResponseWrapper(battles,t);
        return new ResponseEntity<>(responseWrapper, getHeaders(), HttpStatus.CREATED);
    }

    private List<Long> getBattles(Long variableValue) {
        return webClient.get()
                .uri(builder -> builder
                        .path("http://localhost:8082/api/battle/nome-servizio")
                        .queryParam("variableName", variableValue)
                        .build())
                .retrieve()
                .bodyToFlux(Long.class)
                .collectList()
                .block();
    }
    private ResponseEntity<Object> checkRequest(GetTournamentPageRequest request) {
        if(request.getTournamentId() == null){
            log.error("Invalid tournament id request");
            return new ResponseEntity<>("Invalid tournament id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }


}
