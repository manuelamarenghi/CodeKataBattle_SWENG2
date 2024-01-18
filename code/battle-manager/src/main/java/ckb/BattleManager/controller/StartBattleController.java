package ckb.BattleManager.controller;

import ckb.BattleManager.model.Battle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@Slf4j
public class StartBattleController {
    private final WebClient.Builder webClientBuilder;

    public StartBattleController() {
        this.webClientBuilder = WebClient.builder();
    }

    public void startBattle(Battle battleToStart) {
        ResponseEntity<Object> response = webClientBuilder.build()
                .post()
                .uri("http://github-manager:8083/api/github/create-repo")
                .bodyValue(battleToStart)
                .retrieve()
                .toEntity(Object.class)
                .block();

        if (response == null) {
            log.error("Error starting battle with id: {}. The response is null", battleToStart.getBattleId());
            return;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error starting battle with id: {}. Error {}", battleToStart.getBattleId(), response.getStatusCode());
            return;
        }

        log.info("Battle started with id: {}", battleToStart.getBattleId());
    }
}
