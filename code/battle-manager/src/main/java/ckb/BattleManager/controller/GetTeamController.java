package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.GetBattleRequest;
import ckb.BattleManager.dto.output.TeamInfoMessage;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.service.BattleService;
import ckb.BattleManager.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/api/battle")
@Slf4j
public class GetTeamController {
    private final WebClient.Builder webClientBuilder;
    private final BattleService battleService;
    private final TeamService teamService;
    private String url = "http://TournamentManager:8083/api/github/create-repo";

    @Autowired
    public GetTeamController(BattleService battleService, TeamService teamService) {
        this.battleService = battleService;
        this.teamService = teamService;
        this.webClientBuilder = WebClient.builder();
    }

    /**
     * Method to get a team
     *
     * @param idTeam id of the team
     * @return a ResponseEntity with the team or a not found status
     */
    @GetMapping("/get-team")
    public ResponseEntity<TeamInfoMessage> getTeam(@RequestBody GetBattleRequest idTeam) {
        log.info("[API REQUEST] Get team request with id: {}", idTeam.getBattleId());
        try {
            Team team = teamService.getTeam(idTeam.getBattleId());
            Hibernate.initialize(team.getParticipation());
            List<String> participationName = team.getParticipation()
                    .stream()
                    .map(
                            participation -> webClientBuilder.build()
                                    .post()
                                    .uri(url)
                                    .bodyValue(
                                            participation.getParticipationId().getStudentId()
                                    )
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .block()
                    ).toList();
            return ResponseEntity.ok(new TeamInfoMessage(participationName, team.getTeamId(), team.getScore()));
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
    @GetMapping("/get-teams-battle")
    public ResponseEntity<List<Team>> getTeamsOfBattle(@RequestBody GetBattleRequest idBattle) {
        log.info("[API REQUEST] Get teams of battle request with id: {}", idBattle.getBattleId());
        try {
            return ResponseEntity.ok(battleService.getAllTeamsOfBattle(idBattle.getBattleId()));
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
