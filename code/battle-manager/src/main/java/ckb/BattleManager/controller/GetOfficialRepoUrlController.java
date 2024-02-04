package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.OfficialRepoRequest;
import ckb.BattleManager.dto.output.EvaluationParamsResponse;
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

    @PostMapping("/api/battle/evaluation-params")
    public ResponseEntity<Object> evaluationParams(@RequestBody OfficialRepoRequest officialRepoRequest) {
        log.info("[API REQUEST] Official repo request with team id: {}", officialRepoRequest.getTeamId());

        try {
            EvaluationParamsResponse paramsResponse = battleService.getBattleParams(officialRepoRequest.getTeamId());
            log.info("Battle params: {}", paramsResponse.toString());
            return ResponseEntity.ok(paramsResponse);
        } catch (Exception e) {
            log.error("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
