package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.dto.in.EvaluationRequest;
import ckb.SolutionEvaluationService.dto.out.OfficialRepoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
public abstract class Controller {
    String battleManagerUrl = "http://battle-manager:8082";
    private final WebClient webClient = WebClient.create();

    public abstract ResponseEntity<Object> evaluate(EvaluationRequest request);
    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return headers;
    }

    public String getOfficialRepoUrl(EvaluationRequest request) {
        OfficialRepoRequest officialRepoRequest = OfficialRepoRequest.builder()
                .teamId(request.getTeamId())
                .build();
        ResponseEntity<String> response = webClient.post()
                .uri(battleManagerUrl + "/api/battle/official-repo-url")
                .bodyValue(officialRepoRequest)
                .retrieve()
                .toEntity(String.class)
                .block();
        if (response == null || !response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Error getting official repo url");
            throw new RuntimeException("Error getting official repo url");
        } else {
            log.info("Official repo url: " + response.getBody());
            return response.getBody();
        }
    }

    abstract int executeTests(String path, String officialRepoUrl);
    abstract boolean compile(String path);
    abstract int executeStaticAnalysis(String path);
    void initTestMode() {
        battleManagerUrl = "http://localhost:8082";
    }
}
