package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.GetAllTournamentsRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/tournament/get-all-tournaments")
@RequiredArgsConstructor
public class GetAllTournamentsController extends Controller {
    private final TournamentService tournamentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> getTournaments(@RequestBody GetAllTournamentsRequest request) {
        // check if the request has valid data
        System.out.println("sending information");
        List<Tournament> t = tournamentService.getAllTournaments();
        HashMap<Long, String> answer = new HashMap<>();
        for (Tournament tournament : t) {
            answer.put(tournament.getTournamentID(), tournament.getName());
        }
        System.out.println(answer);
        return new ResponseEntity<>(answer, getHeaders(), HttpStatus.CREATED);
    }

}
