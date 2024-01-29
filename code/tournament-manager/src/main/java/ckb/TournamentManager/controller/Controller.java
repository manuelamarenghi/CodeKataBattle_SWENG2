package ckb.TournamentManager.controller;

import org.springframework.http.HttpHeaders;

public abstract class Controller {
    String mailServiceUri = "http://mail-service:8085";
    String accountManagerUri = "http://account-manager:8086";
    String battleManagerUri = "http://battle-manager:8082";

    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return headers;
    }

    void initTestMode() {
        mailServiceUri = "http://localhost:8085";
        accountManagerUri = "http://localhost:8086";
        battleManagerUri = "http://localhost:8082";
    }
}