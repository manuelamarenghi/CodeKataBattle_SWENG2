package ckb.SolutionEvaluationService.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public abstract class Controller {
    public abstract ResponseEntity<Object> evaluate(String repoUrl);
    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return headers;
    }
}
