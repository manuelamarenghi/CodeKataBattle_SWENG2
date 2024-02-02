package ckb.BattleManager.controller;

public abstract class Controller {
    static String githubManagerUri = "http://github-manager";
    static String mailServiceUri = "http://mail-service";
    static String accountManagerUri = "http://account-manager";
    static String tournamentManagerUri = "http://tournament-manager";

    void initTestMode() {
        githubManagerUri = "http://localhost:8083";
        mailServiceUri = "http://localhost:8085";
        accountManagerUri = "http://localhost:8086";
        tournamentManagerUri = "http://localhost:8087";
    }

}
