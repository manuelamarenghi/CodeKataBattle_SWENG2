package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.StudentTeam;
import ckb.BattleManager.dto.output.DirectMailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/api/battle")
@Slf4j
public class InviteStudentToTeamController {
    private final WebClient.Builder webClientBuilder;
    private String url = "http://mail-service:8085/api/mail/direct";

    public InviteStudentToTeamController() {
        this.webClientBuilder = WebClient.builder();
    }

    /**
     * Invite a student to a Team: send a request to the mail service to send an email
     * to the student with the link to join the team
     *
     * @param request
     * @return
     */
    @PostMapping("/invite-student-to-team")
    public ResponseEntity<Object> inviteStudentToTeam(@RequestBody StudentTeam request) {
        log.info("[API REQUEST] Invite student to team request with id_team: {}, id_student: {}", request.getIdTeam(), request.getIdStudent());
        String response = webClientBuilder.build()
                .post()
                .uri(url)
                .bodyValue(
                        // TODO: change the link
                        new DirectMailRequest(List.of(request.getIdStudent().toString()),
                                "You have been invited to join the team: " + request.getIdTeam()
                                        + ". Please join the team by clicking on the link below:\n" +
                                        "link: " + "123456789"
                        )
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info("Response: {}", response);
        return ResponseEntity.ok().build();
    }

    public void initDebug() {
        url = "http://localhost:8085/api/mail/direct";
    }
}
