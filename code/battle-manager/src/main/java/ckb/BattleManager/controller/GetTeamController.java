package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.GetTeamStudentRequest;
import ckb.BattleManager.dto.input.GetTeamsRequest;
import ckb.BattleManager.dto.output.TeamInfoMessage;
import ckb.BattleManager.dto.output.TeamsRankingMessage;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.model.User;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/battle")
@Slf4j
public class GetTeamController extends Controller {
    private final WebClient.Builder webClientBuilder;
    private final BattleService battleService;

    @Autowired
    public GetTeamController(BattleService battleService) {
        this.battleService = battleService;
        this.webClientBuilder = WebClient.builder();
    }

    /**
     * Method to get all the participant of a team knowing only the id of the battle
     * and the id of a student
     *
     * @param request id of the team
     * @return a ResponseEntity with the team or a not found status
     */
    @GetMapping("/get-team")
    public ResponseEntity<TeamInfoMessage> getTeam(@RequestBody GetTeamStudentRequest request) {
        log.info("[API REQUEST] Get team request with id: {}", request.getBattleId());
        try {
            Team team = battleService.getListParticipation(request.getBattleId(), request.getStudentId());

            List<String> participationNames = team.getParticipation()
                    .stream()
                    .map(
                            participation -> {
                                try {
                                    return getNameOfStudent(participation);
                                } catch (Exception e) {
                                    log.error("[EXCEPTION] Error occurred while getting name of student {} : {}",
                                            participation.getStudentId(), e.getMessage());
                                    return null;
                                }
                            }
                    )
                    .filter(Objects::nonNull)
                    .toList();

            List<String> participantNames = new ArrayList<>();
            int errors = 0;
            for (Participation participation : team.getParticipation()) {
                try {
                    participantNames.add(getNameOfStudent(participation));
                } catch (Exception e) {
                    log.error("[EXCEPTION] Error occurred while getting name of student {} : {}",
                            participation.getStudentId(), e.getMessage());
                    errors++;
                }
            }

            if (errors > 0) {
                log.warn("Failed to get all the components of the team {} : {}", team.getTeamId(), participantNames);
            } else {
                log.info("Successfully get all the components of the team {} : {}", team.getTeamId(), participantNames);
            }

            return ResponseEntity.ok(new TeamInfoMessage(participationNames, team.getTeamId()));
        } catch (Exception e) {
            log.error("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Method to get all the teams of a battle and their score
     *
     * @param request id of the battle
     * @return a ResponseEntity with the list of ids of the teams
     */
    @PostMapping("/get-teams-battle")
    public ResponseEntity<TeamsRankingMessage> getTeamsOfBattle(@RequestBody GetTeamsRequest request) {
        log.info("[API REQUEST] Get teams of battle request with id: {}", request.getBattleId());
        try {
            return ResponseEntity.ok(
                    new TeamsRankingMessage(battleService.getAllTeamsOfBattle(request.getBattleId()))
            );
        } catch (Exception e) {
            log.error("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private String getNameOfStudent(Participation participation) throws Exception {
        ResponseEntity<User> user = webClientBuilder.build()
                .post()
                .uri(accountManagerUri + "/api/account/user")
                .bodyValue(
                        participation.getStudentId()
                )
                .retrieve()
                .toEntity(User.class)
                .block();

        if (user == null || user.getBody() == null) {
            log.error("User not found for id {}", participation.getStudentId());
            throw new Exception("User not found for id " + participation.getStudentId());
        }

        return user.getBody().getFullName();
    }
}
