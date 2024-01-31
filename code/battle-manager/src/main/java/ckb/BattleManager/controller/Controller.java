package ckb.BattleManager.controller;

public abstract class Controller {
    String githubManagerUri = "http://github-manager:8083";
    String mailServiceUri = "http://mail-service:8085";
    String accountManagerUri = "http://account-manager:8086";
    String tournamentManagerUri = "http://tournament-manager:8087";

    void initTestMode() {
        githubManagerUri = "http://localhost:8083";
        mailServiceUri = "http://localhost:8085";
        accountManagerUri = "http://localhost:8086";
        tournamentManagerUri = "http://localhost:8087";
    }

}
