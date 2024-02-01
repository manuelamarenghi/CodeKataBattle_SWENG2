package ckb.BattleManager.controller;

import ckb.BattleManager.dto.output.CreateRepoRequest;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.WorkingPair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@Slf4j
public class CreateGHRepositoryBattleController extends Controller {
    private final WebClient webClient = WebClient.create();

    public String createGHRepository(Battle battleToStart, List<WorkingPair<String, String>> files) throws Exception {
        ResponseEntity<String> response = webClient.post()
                .uri(githubManagerUri + "/api/github/create-repo")
                .bodyValue(
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

        log.info("Battle {} repository created", battleToStart.getName());
        return response.getBody();
    }
}
