package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.StudentTeam;
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
public class RegisterStudentToTeamController {
    private final TeamService teamService;

    @Autowired
    public RegisterStudentToTeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/registerStudentToTeam")
    public ResponseEntity<Object> registerStudentToTeam(@RequestBody StudentTeam request) {
        log.info("[API REQUEST] Register student to team request with id_team: {}, id_student: {}", request.getIdTeam(), request.getIdStudent());
        try {
            teamService.registerStudentToTeam(request.getIdStudent(), request.getIdTeam());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
