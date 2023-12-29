package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.PermissionRequest;
import ckb.TournamentManager.dto.SubscriptionRequest;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        //manda mail a user per informarlo
        return new ResponseEntity<>("Permission inserted", getHeaders(), HttpStatus.CREATED);
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
        // bisogna controllare che lo user sia un educator nell'account db
        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }
}
