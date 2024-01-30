package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.InformStudentsRequest;
import ckb.TournamentManager.dto.outcoming.DirectMailRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/tournament/inform-students")
public class InformStudentsController extends Controller {
    private final WebClient webClient = WebClient.create();

    @Autowired
    private TournamentService tournamentService;

    @PostMapping
    public ResponseEntity<Object> informStudents(@RequestBody InformStudentsRequest request) {
        Tournament tournament = tournamentService.getTournament(request.getTournamentId());
        String content = "A new battle called " + request.getBattleName() + " was created in the" +
                " tournament " + tournament.getName() + ".";

        ResponseEntity<String> response = webClient.post()
                .uri(mailServiceUri + "/api/mail/direct")
                .bodyValue(
                        new DirectMailRequest(
                                tournamentService.getStudentsSubscribed(request.getTournamentId())
                                        .stream()
                                        .map(Object::toString).toList(),
                                content
                        )
                )
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response == null || response.getStatusCode().is4xxClientError()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
