package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.PermissionRequest;
import ckb.TournamentManager.dto.SubscriptionRequest;
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
@RequestMapping("/api/tournament/permission")
@RequiredArgsConstructor

public class PermissionController extends Controller{
    private final TournamentService tournamentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> permission(@RequestBody PermissionRequest request) {
        // check if the request has valid data
        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;
        String content = "You've gained permission to create battles in tournament: "+tournamentService.addPermission(request);
        log.info("Permission inserted");
        sendRequest("http://localhost:8085/api/mail/direct", content, request.getUserId())
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
        return new ResponseEntity<>("Permission inserted", getHeaders(), HttpStatus.CREATED);
    }

    private Mono<String> sendRequest(String s, String content, Long userId) {
        return WebClient.create()
                .post()
                .uri(builder -> builder
                        .path(s)
                        .queryParam("variableName", userId)
                        .build())
                .bodyValue(content)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Errore durante la chiamata HTTP")))
                .bodyToMono(String.class);
    }

    private ResponseEntity<Object> checkRequest(PermissionRequest request) {
        if(request.getUserId() == null){
            log.error("Invalid user id request");
            return new ResponseEntity<>("Invalid user id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(tournamentService.getTournament(request.getTournamentId()) == null){
            log.error("Invalid tournament id request");
            return new ResponseEntity<>("Invalid tournament id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(tournamentService.getTournament(request.getTournamentId()).getStatus() == false){
            log.error("Tournament already ended");
            return new ResponseEntity<>("Tournament already ended", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }
}
