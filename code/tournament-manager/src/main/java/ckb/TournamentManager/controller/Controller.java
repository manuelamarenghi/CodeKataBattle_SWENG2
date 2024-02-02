package ckb.TournamentManager.controller;

import org.springframework.http.HttpHeaders;

public abstract class Controller {
    String mailServiceUri = "http://mail-service";
    String accountManagerUri = "http://account-manager";
    String battleManagerUri = "http://battle-manager";

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