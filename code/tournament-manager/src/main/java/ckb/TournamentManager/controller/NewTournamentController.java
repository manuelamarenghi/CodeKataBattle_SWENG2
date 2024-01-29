package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.NewTournamentRequest;
import ckb.TournamentManager.dto.outcoming.AllStudentsMailRequest;
import ckb.TournamentManager.model.Role;
import ckb.TournamentManager.model.User;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@Slf4j
@RequestMapping("/api/tournament/new-tournament")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NewTournamentController extends Controller {
     private final TournamentService tournamentService;
    private final WebClient webClient = WebClient.builder().build();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> newTournament(@RequestBody NewTournamentRequest request) {
        // check if the request has valid data
        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;

        String content = " Hi! A new tournament is created click here to subscribe " + tournamentService.createTournament(request);
        log.info("New tournament created");

        try {
            sendRequest(content);
            log.info("Mail correctly sent!");
        } catch (Exception e) {
            log.error("Error while retrieving send request to mail service: {}\n", e.getMessage());
            return new ResponseEntity<>(getHeaders(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Tournament created", getHeaders(), HttpStatus.CREATED);
    }

    private void sendRequest(String content) throws Exception {
        ResponseEntity<String> answer = webClient
                .post()
                .uri(mailServiceUri + "/api/mail/all-students")
                .bodyValue(new AllStudentsMailRequest(content))
                .retrieve()
                .toEntity(String.class)
                .block();

        if (answer == null || answer.getStatusCode().is4xxClientError()) {
            throw new Exception();
        }
    }


    private ResponseEntity<Object> checkRequest(NewTournamentRequest request) {
        if (request.getRegdeadline() == null) {
            log.error("Registration deadline is null");
            return new ResponseEntity<>("Registration deadline is null", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (request.getCreatorID() == null) {
            log.error("Creator ID is null");
            return new ResponseEntity<>("Creator ID is null", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if(request.getRegdeadline().before(new java.util.Date())){
            log.error("Registration deadline is in the past");
            return new ResponseEntity<>("Registration deadline is in the past", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<User> responseEntityUser = webClient.post()
                .uri(accountManagerUri + "/api/account/user")
                .bodyValue(request.getCreatorID())
                .retrieve()
                .toEntity(User.class)
                .block();

        if (responseEntityUser == null || responseEntityUser.getStatusCode().is4xxClientError()) {
            log.error("Creator not found");
            return new ResponseEntity<>("Creator not found", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (responseEntityUser.getBody() == null || responseEntityUser.getBody().getRole() != Role.EDUCATOR) {
            log.error("Creator is not an educator");
            return new ResponseEntity<>("Creator is not an educator", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }

}
