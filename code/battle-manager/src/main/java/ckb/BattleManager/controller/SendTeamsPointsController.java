package ckb.BattleManager.controller;

import ckb.BattleManager.dto.output.BattleFinishedMessage;
import ckb.BattleManager.model.Battle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@Slf4j
public class SendTeamsPointsController extends Controller {
    private final WebClient.Builder webClientBuilder;
    public SendTeamsPointsController() {
        this.webClientBuilder = WebClient.builder();
    }

    public void sendIdUsersPointsFinishedBattle(Battle battle, List<Pair<Long, Integer>> pairsIdUserPoints) {
        ResponseEntity<Object> response = webClientBuilder.build()
                .post()
                .uri(tournamentManagerUri + "/api/tournament/update-score")
                .bodyValue(
                        new BattleFinishedMessage(
                                battle.getTournamentId(),
                                pairsIdUserPoints
                        )
                )
                .retrieve()
                .toEntity(Object.class)
                .block();

        if (response == null) {
            log.error("Error sending idUsers and points of the finished battle with id: {}. The response is null", battle.getBattleId());
            return;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Error sending idUsers and points of the finished battle with id: {}. Error {}", battle.getBattleId(), response.getStatusCode());
            return;
        }

        log.info("Successfully sent IdUsers and points of the finished battle with id: {}", battle.getBattleId());
    }

}
