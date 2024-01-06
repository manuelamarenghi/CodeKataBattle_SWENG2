package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.PermissionRequest;
import ckb.TournamentManager.dto.outcoming.DirectMailRequest;
import ckb.TournamentManager.dto.outcoming.UserRequest;
import ckb.TournamentManager.model.Role;
import ckb.TournamentManager.model.User;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@Slf4j
@RequestMapping("/api/tournament/permission")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class PermissionController extends Controller{
    private final TournamentService tournamentService;
    @Autowired
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> permission(@RequestBody PermissionRequest request) {
        // check if the request has valid data
        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;
        User e = checkEducator(request);
        if(e == null || e.getRole() != Role.EDUCATOR){
            log.error("Invalid Request");
            return new ResponseEntity<>("Invalid Request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        else{
        String content = "You've gained permission to create battles in tournament: "+tournamentService.addPermission(request);
        log.info("Permission inserted");
        try {
            sendRequest("http://localhost:8085/api/mail/direct", content, request.getUserID());
        } catch (Exception exp) {
            log.error("Error while retrieving send request to mail service\n");
            return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Permission inserted", getHeaders(), HttpStatus.CREATED);
    }}

    private User checkEducator(PermissionRequest request) {
        try{ return webClient
                .post()
                .uri("http://localhost:8086/api/account/user")
                .bodyValue(new UserRequest(request.getUserID()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Errore durante la chiamata HTTP")))
                .bodyToMono(User.class).block();
        }
        catch (Exception e){
            return null;
        }
    }

    private Mono<String> sendRequest(String s, String content, Long userId) {
        return webClient
                .post()
                .uri(s)
                .bodyValue(new DirectMailRequest(Collections.singletonList(userId.toString()),content))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Errore durante la chiamata HTTP")))
                .bodyToMono(String.class);
    }

    private ResponseEntity<Object> checkRequest(PermissionRequest request) {
        if(request.getUserID() == null || request.getTournamentID() == null){
            log.error("Invalid user or tournament id request");
            return new ResponseEntity<>("Invalid user or tournament id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(tournamentService.getTournament(request.getTournamentID()) == null){
            log.error("Invalid tournament id request");
            return new ResponseEntity<>("Invalid tournament id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(tournamentService.getTournament(request.getTournamentID()).getStatus() == false){
            log.error("Tournament already ended");
            return new ResponseEntity<>("Tournament already ended", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }
}