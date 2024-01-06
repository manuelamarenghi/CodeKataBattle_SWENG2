package ckb.BattleManager.controller;

import ckb.BattleManager.model.Team;
import ckb.BattleManager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/team")
    public Team getTeam(@RequestBody Long idTeam) {
        return teamService.getTeam(idTeam);
    }

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getTeamsOfBattle(@RequestBody Long idBattle) {
        return ResponseEntity.ok(teamService.getListTeam(idBattle));
    }
}
