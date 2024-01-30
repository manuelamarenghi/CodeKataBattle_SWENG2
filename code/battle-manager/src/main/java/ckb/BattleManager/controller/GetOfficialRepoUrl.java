package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.OfficialRepoRequest;
import ckb.BattleManager.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetOfficialRepoUrl {
    private final BattleService battleService;

    @Autowired
    public GetOfficialRepoUrl(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("/api/battle/official-repo-url")
    public ResponseEntity<String> getOfficialRepoUrl(@RequestBody OfficialRepoRequest officialRepoRequest) {
        try {
            return ResponseEntity.ok(battleService.getOfficialRepo(officialRepoRequest.getTeamId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
