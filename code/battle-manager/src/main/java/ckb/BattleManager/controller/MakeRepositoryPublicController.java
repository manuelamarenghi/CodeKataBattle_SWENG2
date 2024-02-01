package ckb.BattleManager.controller;

import ckb.BattleManager.dto.output.MakePublicRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class MakeRepositoryPublicController extends Controller {
    private final WebClient webClient = WebClient.create();

    public void makeRepositoryPublic(String repositoryName) throws Exception {
        ResponseEntity<String> response = webClient.post()
                .uri(githubManagerUri + "/api/github/make-public")
                .bodyValue(
                        new MakePublicRequest(repositoryName)
                ).retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && response.getStatusCode().is4xxClientError()) {
            log.error("Error making repository public {}", response.getBody());
            throw new Exception("Error making repository public");
        }

        if (response == null || response.getStatusCode().is4xxClientError()) {
            log.error("Error making repository public and the response is null");
            throw new Exception("Error making repository public");
        }
    }
}
