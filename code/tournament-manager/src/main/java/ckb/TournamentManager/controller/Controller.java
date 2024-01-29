package ckb.TournamentManager.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@RestController

public abstract class Controller {
    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return headers;
    }
}