package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.CreateBattleRequest;
import ckb.BattleManager.dto.output.CheckPermissionRequest;
import ckb.BattleManager.dto.output.InformStudentsRequest;
import ckb.BattleManager.dto.output.UserRequest;
import ckb.BattleManager.model.Role;
import ckb.BattleManager.model.User;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/battle")
@Slf4j
public class CreateBattleController {
    private final BattleService battleService;
    private final WebClient webClient = WebClient.create();
    private String accountManagerUri = "http://account-manager:8086";
    private String tournamentManagerUri = "http://tournament-manager:8087";

    @Autowired
    public CreateBattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("/create-battle")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createBattle(@RequestBody CreateBattleRequest request) {
        log.info("[API REQUEST] Create battle request: {}", request);

        try {
            checkBattleRequest(request);
            battleService.createBattle(request);
            informAllStudentsOfTournament(request.getTournamentId(), request.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private void informAllStudentsOfTournament(Long tournamentId, String battleName) throws Exception {
        ResponseEntity<Object> response = webClient.post()
                .uri(tournamentManagerUri + "/api/tournament/inform-students")
                .bodyValue(new InformStudentsRequest(
                        tournamentId,
                        battleName
                ))
                .retrieve()
                .toEntity(Object.class)
                .block();

        if (response == null || response.getStatusCode().is4xxClientError()) {
            log.error("[ERROR] Inform students of tournament error");
            throw new Exception("Inform students of tournament error");
        }
    }

    private void checkBattleRequest(CreateBattleRequest request) throws Exception {
        if (request == null) {
            log.error("[ERROR] Battle request is null");
            throw new Exception("Battle request is null");
        }

        if (request.getTournamentId() == null || request.getAuthorId() == null
                || request.getMinStudents() == null || request.getMaxStudents() == null
                || request.getRegDeadline() == null || request.getSubDeadline() == null
                || request.getName() == null) {
            log.error("[ERROR] One of the field is null, the request is invalid");
            throw new Exception("One of the field is null, the request is invalid");
        }

        if (request.getTournamentId() <= 0) {
            log.error("[ERROR] Tournament id must be greater than 0");
            throw new Exception("Tournament id must be greater than 0");
        }

        if (request.getAuthorId() <= 0) {
            log.error("[ERROR] Author id must be greater than 0");
            throw new Exception("Author id must be greater than 0");
        }

        if (request.getMinStudents() <= 0) {
            log.error("[ERROR] Min students must be greater than 0");
            throw new Exception("Min students must be greater than 0");
        }

        if (request.getMaxStudents() <= 0) {
            log.error("[ERROR] Max students must be greater than 0");
            throw new Exception("Max students must be greater than 0");
        }

        if (request.getMaxStudents() < request.getMinStudents()) {
            log.error("[ERROR] Max students must be greater than min students");
            throw new Exception("Max students must be greater than min students");
        }

        if (request.getRegDeadline().isBefore(LocalDateTime.now())) {
            log.error("[ERROR] Registration deadline must be in the future");
            throw new Exception("Registration deadline must be in the future");
        }

        if (request.getSubDeadline().isBefore(LocalDateTime.now())) {
            log.error("[ERROR] Submission deadline must be in the future");
            throw new Exception("Submission deadline must be in the future");
        }

        if (request.getRegDeadline().isAfter(request.getSubDeadline())) {
            log.error("[ERROR] Registration deadline must be before submission deadline");
            throw new Exception("Registration deadline must be before submission deadline");
        }

        ResponseEntity<User> responseEntityUser = webClient.post()
                .uri(accountManagerUri + "/api/account/user")
                .bodyValue(new UserRequest(request.getAuthorId()))
                .retrieve()
                .toEntity(User.class)
                .block();

        if (responseEntityUser == null || responseEntityUser.getStatusCode().is4xxClientError()) {
            log.error("[ERROR] User not found");
            throw new Exception("User not found");
        }

        if (responseEntityUser.getBody() == null || responseEntityUser.getBody().getRole() != Role.EDUCATOR) {
            log.error("[ERROR] User is not an educator");
            throw new Exception("User is not an educator");
        }

        log.info("TournamentId {} ; AuthorId {}", request.getTournamentId(), request.getAuthorId());
        ResponseEntity<Object> responseEntityPermission = webClient.post()
                .uri(tournamentManagerUri + "/api/tournament/check-permission")
                .bodyValue(new CheckPermissionRequest(request.getTournamentId(), request.getAuthorId()))
                .retrieve()
                .toEntity(Object.class)
                .block();

        if (responseEntityPermission == null || responseEntityPermission.getStatusCode().is4xxClientError()) {
            log.error("[ERROR] Permission not found");
            throw new Exception("Permission not found");
        }
    }

    public void initTestMode() {
        accountManagerUri = "http://localhost:8086";
        tournamentManagerUri = "http://localhost:8087";
    }
}
