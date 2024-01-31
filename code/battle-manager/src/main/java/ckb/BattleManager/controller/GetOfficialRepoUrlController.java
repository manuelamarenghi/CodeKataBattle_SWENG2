package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.OfficialRepoRequest;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GetOfficialRepoUrlController {
    private final BattleService battleService;

    @Autowired
    public GetOfficialRepoUrlController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("/api/battle/official-repo-url")
    public ResponseEntity<String> getOfficialRepoUrl(@RequestBody OfficialRepoRequest officialRepoRequest) {
        log.info("[API REQUEST] Official repo request with team id: {}", officialRepoRequest.getTeamId());

        try {
            String officialRepo = battleService.getOfficialRepo(officialRepoRequest.getTeamId());
            log.info("Official repo of the team id {} is {}", officialRepoRequest.getTeamId(), officialRepo);
            return ResponseEntity.ok(officialRepo);
        } catch (Exception e) {
            log.error("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
