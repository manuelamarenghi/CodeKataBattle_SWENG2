package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.IdLong;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.service.BattleService;
import ckb.BattleManager.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/battle")
@Slf4j
public class GetTeamController {
    private final BattleService battleService;
    private final TeamService teamService;

    @Autowired
    public GetTeamController(BattleService battleService, TeamService teamService) {
        this.battleService = battleService;
        this.teamService = teamService;
    }

    /**
     * Method to get a team
     *
     * @param idTeam id of the team
     * @return a ResponseEntity with the team or a not found status
     */
    @GetMapping("/getTeam")
    public ResponseEntity<Team> getTeam(@RequestBody IdLong idTeam) {
        log.info("[API REQUEST] Get team request with id: {}", idTeam.getId());
        try {
            return ResponseEntity.ok(teamService.getTeam(idTeam.getId()));
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Method to get all the teams of a battle
     *
     * @param idBattle id of the battle
     * @return a ResponseEntity with the list of ids of the teams
     */
    @GetMapping("/getTeamsOfBattle")
    public ResponseEntity<List<Team>> getTeamsOfBattle(@RequestBody IdLong idBattle) {
        log.info("[API REQUEST] Get teams of battle request with id: {}", idBattle.getId());
        try {
            return ResponseEntity.ok(battleService.getAllTeamsOfBattle(idBattle.getId()));
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
