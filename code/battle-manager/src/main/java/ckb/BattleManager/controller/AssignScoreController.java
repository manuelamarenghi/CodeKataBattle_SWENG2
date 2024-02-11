package ckb.BattleManager.controller;

import ckb.BattleManager.dto.in.AssignPersonalScoreRequest;
import ckb.BattleManager.dto.in.AssignScoreRequest;
import ckb.BattleManager.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    /**
     * Method used to assign a score to a team
     *
     * @param request a pair team and score
     * @return a ResponseEntity
     */
    @PostMapping("/assign-score")
    public ResponseEntity<Object> assignScore(@RequestBody AssignScoreRequest request) {
        log.info("[API REQUEST] Assign score request with id_team: {}, score: {}", request.getIdTeam(), request.getScore());
        try {
            teamService.assignScore(request.getIdTeam(), request.getScore());
            log.info("Successfully assigned score {} to team {}", request.getScore(), request.getIdTeam());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/assign-personal-score")
    public ResponseEntity<Object> assignPersonalScore(@RequestBody AssignPersonalScoreRequest request) {
        log.info("[API REQUEST] Assign personal score request with id_team: {}, score: {} by educator {}", request.getIdTeam(), request.getScore(), request.getIdEducator());
        try {
            teamService.assignPersonalScore(request.getIdTeam(), request.getScore(), request.getIdEducator());
            log.info("Successfully assigned personal score {} to team {} by educator {}", request.getScore(), request.getIdTeam(), request.getIdEducator());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
