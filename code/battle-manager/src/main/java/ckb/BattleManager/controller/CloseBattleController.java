package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.CloseBattleRequest;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import ckb.BattleManager.service.TeamService;
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
    private final TeamService teamService;
    private final SendMailsToParticipants sendMailsToParticipants;
    private final SendTeamsPointsController sendTeamsPointsController;

    @Autowired
    public CloseBattleController(BattleService battleService, TeamService teamService,
                                 SendMailsToParticipants sendMailsToParticipants,
                                 SendTeamsPointsController sendTeamsPointsController) {
        this.battleService = battleService;
        this.teamService = teamService;
        this.sendMailsToParticipants = sendMailsToParticipants;
        this.sendTeamsPointsController = sendTeamsPointsController;
    }

    @PostMapping("/api/battle/close-battle")
    public ResponseEntity<Object> closeBattle(@RequestBody CloseBattleRequest request) {
        try {
            Battle battle = battleService.closeBattle(request.getBattleId(), request.getEducatorId());
            log.info("Successfully closed battle");

            sendTeamsPointsController
                    .sendIdUsersPointsFinishedBattle(battle, teamService.getListPairIdUserPoints(battle));
            sendMailsToParticipants.send(battle);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error closing battle: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
