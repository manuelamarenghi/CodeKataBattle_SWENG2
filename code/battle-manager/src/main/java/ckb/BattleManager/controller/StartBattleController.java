package ckb.BattleManager.controller;

import ckb.BattleManager.model.Battle;
import lombok.extern.slf4j.Slf4j;
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
        webClientBuilder.build()
                .post()
                .uri("http://github-service:port/api/start-battle")
                .body(battleToStart, Battle.class);
        //.retrieve()
        //.bodyToMono(String.class)
        //.block();
        log.info("Battle started with id: {}", battleToStart.getBattleId());
    }
}
