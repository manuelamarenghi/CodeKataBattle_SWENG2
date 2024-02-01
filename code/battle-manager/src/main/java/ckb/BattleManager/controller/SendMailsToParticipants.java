package ckb.BattleManager.controller;

import ckb.BattleManager.dto.output.DirectMailRequest;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void send(Battle battle) {
        webClient.post()
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
                .toEntity(Object.class)
                .block();

        log.info("Successfully sent emails to the participant of the battle {}", battle.getName());
    }
}
