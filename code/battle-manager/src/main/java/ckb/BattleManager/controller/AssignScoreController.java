package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.PairTeamScore;
import ckb.BattleManager.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/battle")
@Slf4j
public class AssignScoreController {
    private final TeamService teamService;

    @Autowired
    public AssignScoreController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/assignScore")
    public ResponseEntity<Object> assignScore(@RequestBody PairTeamScore request) {
        log.info("[API REQUEST] Assign score request with id_team: {}, score: {}", request.getIdTeam(), request.getScore());
        try {
            teamService.assignScore(request.getIdTeam(), request.getScore());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/assignPersonalScore")
    public ResponseEntity<Object> assignPersonalScore(@RequestBody PairTeamScore request) {
        log.info("[API REQUEST] Assign personal score request with id_team: {}, score: {}", request.getIdTeam(), request.getScore());
        try {
            teamService.assignPersonalScore(request.getIdTeam(), request.getScore());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
