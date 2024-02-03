package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.InformStudentsRequest;
import ckb.TournamentManager.dto.outcoming.DirectMailRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.service.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/tournament/inform-students")
@Slf4j
public class InformStudentsController extends Controller {
    private final WebClient webClient = WebClient.create();

    @Autowired
    private TournamentService tournamentService;

    @PostMapping
    public ResponseEntity<Object> informStudents(@RequestBody InformStudentsRequest request) {
        log.info("[API REQUEST] Inform students of a tournament: {} for the battle {}",
                request.getTournamentId(), request.getBattleName());

        Tournament tournament = tournamentService.getTournament(request.getTournamentId());
        String content = "A new battle called " + request.getBattleName() + " was created in the" +
                " tournament " + tournament.getName() + ".";

        ResponseEntity<String> response = webClient.post()
                .uri(mailServiceUri + "/api/mail/direct")
                .bodyValue(
                        new DirectMailRequest(
                                tournamentService.getStudentsSubscribed(request.getTournamentId())
                                        .stream()
                                        .map(Object::toString)
                                        .toList(),
                                content
                        )
                )
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response == null || response.getStatusCode().is4xxClientError()) {
            log.error("Error while informing students of a tournament: {}", request.getTournamentId());
            return ResponseEntity.badRequest().build();
        }

        log.info("Successfully Informed all the students of a tournament: {} for the battle {}",
                request.getTournamentId(), request.getBattleName());
        return ResponseEntity.ok().build();
    }
}
