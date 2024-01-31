package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.GetTournamentPageRequest;
import ckb.TournamentManager.dto.outcoming.GetBattlesRequest;
import ckb.TournamentManager.dto.outcoming.ListBattlesResponse;
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
@RequestMapping("/api/tournament/get-tournament-page")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class GetTournamentPageController extends Controller {
    private final TournamentService tournamentService;

    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseWrapper> getTournamentPage(@RequestBody GetTournamentPageRequest request) {
        if (invalidRequest(request)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<TournamentRanking> t = tournamentService.getTournamentPage(request);
        ResponseWrapper responseWrapper;

        try {
            log.info("Tournament page retrieved");
            List<Long> battles = getBattles(request.getTournamentID());
            responseWrapper = new ResponseWrapper(battles, t);
        } catch (Exception e) {
            log.error("[EXCEPTION] An exception occurred while retrieving battles {}", e.toString());
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(responseWrapper, getHeaders(), HttpStatus.CREATED);
    }

    private List<Long> getBattles(Long tournamentID) throws Exception {
        ResponseEntity<ListBattlesResponse> response = webClient
                .post()
                .uri(battleManagerUri + "/api/battle/get-battles-tournament")
                .bodyValue(
                        new GetBattlesRequest(
                                tournamentID
                        )
                )
                .retrieve()
                .toEntity(ListBattlesResponse.class)
                .block();

        if (response == null) {
            log.error("[ERROR] The response is null");
            throw new Exception("The response is null");
        }

        if (response.getStatusCode().is4xxClientError()) {
            log.error("[ERROR] The response has an error {}", response.getBody());
            throw new Exception("The response has an error");
        }

        if (response.getBody() == null) {
            log.error("[ERROR] The response body is null");
            throw new Exception("The response body is null");
        }

        log.info("Successfully retrieved the list of battles");
        return response.getBody().getBattlesID();
    }

    private boolean invalidRequest(GetTournamentPageRequest request) {
        if (request.getTournamentID() == null || tournamentService.getTournament(request.getTournamentID()) == null) {
            log.error("Invalid tournament id request");
            return true;
        }
        return false;
    }

}
