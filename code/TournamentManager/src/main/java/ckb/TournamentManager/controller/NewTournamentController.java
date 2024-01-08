package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.NewTournamentRequest;
import ckb.TournamentManager.dto.outcoming.AllStudentsMailRequest;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/api/tournament/new-tournament")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

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
        try {
            sendRequest("http://localhost:8085/api/mail/all-students", content);
            log.info("Mail correctly sent!");
        } catch (Exception e) {
            log.error("Error while retrieving send request to mail service\n");
            return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Tournament created", getHeaders(), HttpStatus.CREATED);
    }

    private void sendRequest(String s, String content) {
        Mono<String> c = WebClient.create()
                .post()
                .uri(s)
                .bodyValue(new AllStudentsMailRequest(content))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Errore durante la chiamata HTTP")))
                .bodyToMono(String.class);
    }


    private ResponseEntity<Object> checkRequest(NewTournamentRequest request) {
        if (request.getRegdeadline() == null) {
            log.error("Invalid request");
            return new ResponseEntity<>("Invalid data request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(request.getRegdeadline().before(new java.util.Date())){
            log.error("Invalid request");
            return new ResponseEntity<>("Invalid data request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }


}
