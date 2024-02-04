package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.SubscriptionRequest;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/tournament/subscription")
@RequiredArgsConstructor

public class SubscriptionController extends Controller{
    private final TournamentService tournamentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> subscription(@RequestBody SubscriptionRequest request) {
        // check if the request has valid data
        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;
        tournamentService.addSubscription(request);
        log.info("Subscription inserted");
        return new ResponseEntity<>("Subscription inserted", getHeaders(), HttpStatus.CREATED);
    }

    private ResponseEntity<Object> checkRequest(SubscriptionRequest request) {
        if(request.getUserID() == null){
            log.error("Invalid user id request");
            return new ResponseEntity<>("Invalid user id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(tournamentService.getTournament(request.getTournamentID()) == null){
            log.error("Invalid tournament id request");
            return new ResponseEntity<>("Invalid tournament id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(tournamentService.getTournament(request.getTournamentID()).getRegdeadline().before(new java.util.Date())){
            log.error("Reg deadline expired request");
            return new ResponseEntity<>("Reg deadline expired request", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(tournamentService.isSubscribed(request.getTournamentID(),request.getUserID())){
            log.error("User already subscribed");
            return new ResponseEntity<>("User already subscribed", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        if(tournamentService.getTournament(request.getTournamentID()).getStatus() == false){
            log.error("Tournament already ended");
            return new ResponseEntity<>("Tournament already ended", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }
}
