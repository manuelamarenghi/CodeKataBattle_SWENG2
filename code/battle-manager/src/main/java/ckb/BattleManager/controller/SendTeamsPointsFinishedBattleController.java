package ckb.BattleManager.controller;

import ckb.BattleManager.dto.output.BattleFinishedMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
public class SendTeamsPointsFinishedBattleController {
    private final WebClient.Builder webClientBuilder;

    public SendTeamsPointsFinishedBattleController() {
        this.webClientBuilder = WebClient.builder();
    }

    public void sendTeamsPointsFinishedBattle(Long idTournament, List<Pair<Long, Long>> pairsIdUserPoints) {
        webClientBuilder.build()
                .post()
                .uri("http://tournament-service:port/api/send-teams-points-finished-battle")
                .body(
                        new BattleFinishedMessage(
                                idTournament,
                                pairsIdUserPoints
                        ),
                        BattleFinishedMessage.class
                );
    }
}
