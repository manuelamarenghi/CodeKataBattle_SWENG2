package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.NewTournamentRequest;
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
        // mandare mail a tutti gli utenti registrati
        sendRequest("/api/mail/all-students", content)
                .doOnError(error -> {
                    log.error("Error sending mail", error);
                    // Puoi aggiungere ulteriori log o gestione degli errori qui se necessario
                })
                .onErrorResume(throwable -> Mono.just("Error sending mail")) // Fornisce un fallback in caso di errore
                .subscribe(
                        r -> {
                            // Puoi gestire la risposta qui, ad esempio, log o altri passaggi necessari
                            log.info("Mail sent successfully: {}", r);
                        },
                        error -> {
                            // Puoi gestire l'errore qui, ad esempio, log o altri passaggi necessari
                            log.error("Error sending mail", error);
                        }
                );
        return new ResponseEntity<>("Tournament created", getHeaders(), HttpStatus.CREATED);
    }

    private Mono<String> sendRequest(String s, String content) {
        return WebClient.create()
                .post()
                .uri(s)
                .bodyValue(content)
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
