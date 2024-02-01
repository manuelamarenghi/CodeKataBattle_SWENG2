package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.CloseBattleRequest;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CloseBattleController extends Controller {
    private final BattleService battleService;
    private final SendMailsToParticipants sendMailsToParticipants;

    @Autowired
    public CloseBattleController(BattleService battleService, SendMailsToParticipants sendMailsToParticipants) {
        this.battleService = battleService;
        this.sendMailsToParticipants = sendMailsToParticipants;
    }

    @PostMapping("/api/battle/close-battle")
    public ResponseEntity<Object> closeBattle(@RequestBody CloseBattleRequest request) {
        try {
            Battle battle = battleService.closeBattle(request.getBattleId(), request.getEducatorId());
            log.info("Successfully closed battle");
            sendMailsToParticipants.send(battle);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error closing battle: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
