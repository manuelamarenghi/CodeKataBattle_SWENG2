package ckb.BattleManager.controller;

import ckb.BattleManager.dto.output.StartBattleMessage;
import ckb.BattleManager.model.Battle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@Slf4j
public class StartBattleController {
    private final WebClient webClient = WebClient.create();
    private String githubManagerUri = "http://github-manager:8083";


    public String startBattle(Battle battleToStart) throws Exception {
        ResponseEntity<String> response = webClient.post()
                .uri(githubManagerUri + "/api/github/create-repo")
                .bodyValue(
                        new StartBattleMessage(
                                battleToStart.getBattleId()
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

    public void initDebug() {
        githubManagerUri = "http://localhost:8083";
    }
}
