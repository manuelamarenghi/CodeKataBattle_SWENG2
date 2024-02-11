package ckb.BattleManager.controller;

import ckb.BattleManager.dto.out.DirectMailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
public class SendMailsToParticipants extends Controller {
    private final WebClient webClient = WebClient.create();

    public void send(List<Long> participantIds, String content, String battleName) throws Exception {
        ResponseEntity<String> response = webClient.post()
                .uri(mailServiceUri + "/api/mail/direct")
                .bodyValue(
                        new DirectMailRequest(
                                participantIds.stream()
                                        .map(Object::toString)
                                        .toList(),
                                content
                        )
                )
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response == null || response.getStatusCode().is4xxClientError()) {
            log.error("Error sending emails to the participant of the battle {}", battleName);
            throw new Exception("Error sending emails to the participant of the battle " + battleName);
        }

        log.info("Successfully sent emails to the participant of the battle {}", battleName);
    }
}
