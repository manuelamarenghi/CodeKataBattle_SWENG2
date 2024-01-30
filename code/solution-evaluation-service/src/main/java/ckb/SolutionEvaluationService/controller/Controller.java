package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.dto.in.EvaluationRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public abstract class Controller {
    String battleManagerUrl = "http://battle-manager:8082";

    public abstract ResponseEntity<Object> evaluate(EvaluationRequest request);
    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return headers;
    }

    void initTestMode() {
        battleManagerUrl = "http://localhost:8082";
    }
}
