package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.InviteStudentTeamRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InviteStudentToTeamControllerTest {
    private final InviteStudentToTeamController inviteStudentToTeamController;
    private ClientAndServer mockServer;

    @Autowired
    public InviteStudentToTeamControllerTest(InviteStudentToTeamController inviteStudentToTeamController) {
        this.inviteStudentToTeamController = inviteStudentToTeamController;
    }

    @BeforeAll
    public void setUp() {
        mockServer = ClientAndServer.startClientAndServer(8085);
        mockServer
                .when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/mail/direct"))
                .respond(HttpResponse.response().withStatusCode(200));
    }

    @Test
    public void inviteStudentToTeam() {
        inviteStudentToTeamController.initDebug();
        ResponseEntity<Object> response = inviteStudentToTeamController
                .inviteStudentToTeam(
                        new InviteStudentTeamRequest(1L, 1L)
                );

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @AfterAll
    void tearDown() {
        mockServer.stop();
    }
}