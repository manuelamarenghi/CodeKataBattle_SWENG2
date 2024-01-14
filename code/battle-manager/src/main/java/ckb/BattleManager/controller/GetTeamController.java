package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.IdLong;
import ckb.BattleManager.model.Team;
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
    private final TeamService teamService;

    @Autowired
    public GetTeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/getTeam")
    public ResponseEntity<Team> getTeam(@RequestBody IdLong idTeam) {
        log.info("[API REQUEST] Get team request with id: {}", idTeam.getId());
        try {
            return ResponseEntity.ok(teamService.getTeam(idTeam.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/getTeamsOfBattle")
    public ResponseEntity<List<Team>> getTeamsOfBattle(@RequestBody IdLong idBattle) {
        log.info("[API REQUEST] Get teams of battle request with id: {}", idBattle.getId());
        try {
            return ResponseEntity.ok(teamService.getListTeam(idBattle.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
