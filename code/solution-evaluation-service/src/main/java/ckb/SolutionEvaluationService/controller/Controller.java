package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.dto.in.EvaluationParamsResponse;
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

    public EvaluationParamsResponse getOfficialRepoUrl(EvaluationRequest request) {
        OfficialRepoRequest officialRepoRequest = OfficialRepoRequest.builder()
                .teamId(request.getTeamId())
                .build();

        ResponseEntity<EvaluationParamsResponse> response = webClient.post()
                .uri(battleManagerUrl + "/api/battle/evaluation-params")
                .bodyValue(officialRepoRequest)
                .retrieve()
                .toEntity(EvaluationParamsResponse.class)
                .block();

        if (response == null || response.getStatusCode().is4xxClientError()) {
            log.error("Error getting the repo url, {}", response != null ? response.getBody() : null);
            throw new RuntimeException("Repo is private, battle not started yet");
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Error getting official repo url");
            throw new RuntimeException("Error getting official repo url");
        } else {
            log.info("Official repo url: " + response.getBody().getRepoLink());
            log.info("Security: " + response.getBody().isSecurity()
                    + " Reliability: " + response.getBody().isReliability()
                    + " Maintainability: " + response.getBody().isMaintainability());
            return response.getBody();
        }
    }

    abstract int executeTests(String path, String officialRepoUrl);

    abstract boolean compile(String path);

    abstract int executeStaticAnalysis(String path, EvaluationParamsResponse evaluationParamsResponse);

    void initTestMode() {
        battleManagerUrl = "http://localhost:8082";
    }
}
