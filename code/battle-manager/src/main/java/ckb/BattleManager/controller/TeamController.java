package ckb.BattleManager.controller;

import ckb.BattleManager.dto.IdLong;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/team")
@Slf4j
public class TeamController {
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/team")
    public Team getTeam(@RequestBody IdLong idTeam) {
        log.info("[API REQUEST] Get team request with id: {}", idTeam.getId());
        return teamService.getTeam(idTeam.getId());
    }

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getTeamsOfBattle(@RequestBody IdLong idBattle) {
        log.info("[API REQUEST] Get teams of battle request with id: {}", idBattle.getId());
        return ResponseEntity.ok(teamService.getListTeam(idBattle.getId()));
    }
}
