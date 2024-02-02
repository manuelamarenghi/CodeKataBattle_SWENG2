package ckb.MailService.controller;

import org.springframework.http.HttpHeaders;

public abstract class EmailSender {
    String accountManagerUrl = "http://account-manager";
    HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        return headers;
    }

    void initTestMode() {
        accountManagerUrl = "http://localhost:8086";
    }

}
