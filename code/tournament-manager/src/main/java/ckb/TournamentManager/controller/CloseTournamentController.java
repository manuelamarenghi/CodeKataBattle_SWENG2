package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.in.CloseTournamentRequest;
import ckb.TournamentManager.dto.out.AbleToCloseRequest;
import ckb.TournamentManager.dto.out.BattleFinishedResponse;
import ckb.TournamentManager.dto.out.DirectMailRequest;
import ckb.TournamentManager.model.Tournament;
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
@RequestMapping("/api/tournament/close-tournament")
@RequiredArgsConstructor
public class CloseTournamentController extends Controller {
    private final TournamentService tournamentService;
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> closeTournament(@RequestBody CloseTournamentRequest request) {
        // check if the request has valid data
        ResponseEntity<Object> response = checkRequest(request);
        if (response.getStatusCode().is4xxClientError()) return response;

        if (!contactBattleManager(request)) {
            log.error("Could not close tournament because or the there was an error contacting the battle manager or the battles are not closed");
            return new ResponseEntity<>("Not possible to close", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        try {
            tournamentService.closeTournament(request);
            Long tournamentID = request.getTournamentID();
            String tournamentName = tournamentService.getTournament(tournamentID).getName();

            List<String> studentIDs = tournamentService.getStudentsSubscribed(tournamentID).stream().map(Object::toString).toList();
            log.info("Tournament {} is now closed", tournamentName);

            sendMailToAllStudents(studentIDs, tournamentName);

            return new ResponseEntity<>("Tournament closed", getHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), getHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    private void sendMailToAllStudents(List<String> studentIDs, String tournamentName) throws Exception {
        DirectMailRequest request = DirectMailRequest.builder()
                .userIDs(studentIDs)
                .content("Tournament " + tournamentName + "has ended")
                .build();

        ResponseEntity<String> response = webClient.post()
                .uri(mailServiceUri + "/api/mail/direct")
                .bodyValue(request)
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            log.info("Mail sent to all participants of {} to inform them of the tournament ending", tournamentName);
        } else {
            log.error("Failed to send email for ending tournament {}", tournamentName);
            throw new Exception("Failed to send email for ending tournament");
        }
    }

    private boolean contactBattleManager(CloseTournamentRequest request) {
        try {
            ResponseEntity<BattleFinishedResponse> response = webClient
                    .post()
                    .uri(battleManagerUri + "/api/battle/battles-finished")
                    .bodyValue(new AbleToCloseRequest(request.getTournamentID()))
                    .retrieve()
                    .toEntity(BattleFinishedResponse.class)
                    .block();
            return response != null && response.getBody() != null && response.getBody().getAbleToClose();
        } catch (Exception e) {
            log.error("Error contacting battle manager {}", e.getMessage());
            return false;
        }
    }

    private ResponseEntity<Object> checkRequest(CloseTournamentRequest request) {
        if (request.getTournamentID() == null) {
            log.error("The tournament ID or the creator ID is null");
            return new ResponseEntity<>("Invalid tournament id", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (request.getCreatorID() == null) {
            log.error("The creator ID is null");
            return new ResponseEntity<>("Invalid creator id", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        Tournament tournament = tournamentService.getTournament(request.getTournamentID());
        if (tournament == null) {
            log.error("Invalid tournament id request");
            return new ResponseEntity<>("Invalid tournament id request", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        if (!tournament.getCreatorID().equals(request.getCreatorID())) {
            log.error("The creator ID sent is not the creator of the tournament");
            return new ResponseEntity<>("The creator ID sent is not the creator of the tournament", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Valid request", getHeaders(), HttpStatus.OK);
    }

}
