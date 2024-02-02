package ckb.BattleManager.controller;

import ckb.BattleManager.dto.output.DirectMailRequest;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class SendMailsToParticipants extends Controller {
    private final WebClient webClient;
    private final BattleService battleService;

    @Autowired
    public SendMailsToParticipants(BattleService battleService) {
        this.battleService = battleService;
        this.webClient = WebClient.create();
    }

    public void send(Battle battle) throws Exception {
        ResponseEntity<String> response = webClient.post()
                .uri(mailServiceUri + "/api/mail/direct")
                .bodyValue(
                        new DirectMailRequest(
                                battleService.getBattleParticipants(battle).stream()
                                        .map(Object::toString)
                                        .toList(),
                                "The battle " + battle.getName() + " is finished.\n" +
                                        "Check out the ranking on the website"
                        )
                )
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response == null || response.getStatusCode().is4xxClientError()) {
            log.error("Error sending emails to the participant of the battle {}", battle.getName());
            throw new Exception("Error sending emails to the participant of the battle " + battle.getName());
        }

        log.info("Successfully sent emails to the participant of the battle {}", battle.getName());
    }
}
