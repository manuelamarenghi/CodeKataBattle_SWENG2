package ckb.BattleManager.controller;

import ckb.BattleManager.dto.output.CreateRepoRequest;
import ckb.BattleManager.model.Battle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@Slf4j
public class CreateGHRepositoryBattleController extends Controller {
    private final WebClient webClient = WebClient.create();

    public String createGHRepository(Battle battleToStart, List<ImmutablePair<String, String>> files) throws Exception {
        ResponseEntity<String> response = webClient.post()
                .uri(githubManagerUri + "/api/github/create-repo")
                .bodyValue(
                        //TODO name of the battle (the name should be given by the github manager) and files?
                        new CreateRepoRequest(
                                battleToStart.getName(),
                                files
                        )
                )
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response == null) {
            log.error("[ERROR] Error starting battle with id: {}. The response is null", battleToStart.getBattleId());
            throw new Exception("Error starting battle with id: " + battleToStart.getBattleId());
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("[ERROR] Error starting battle with id: {}. Error {}", battleToStart.getBattleId(), response.getStatusCode());
            throw new Exception("Error starting battle with id: " + battleToStart.getBattleId());
        }

        log.info("Battle started with id: {}", battleToStart.getBattleId());
        return response.getBody();
    }
}
