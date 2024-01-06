package ckb.TournamentManager;

import ckb.TournamentManager.controller.PermissionController;
import ckb.TournamentManager.dto.incoming.PermissionRequest;
import ckb.TournamentManager.model.Role;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.model.User;
import ckb.TournamentManager.repo.TournamentRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
public class PermissionControllerTest {
    @Autowired
    private PermissionController permissionController;
    @Autowired
    private TournamentRepo tournamentRepo;
    private static ClientAndServer mockServermail;
    private static ClientAndServer mockServeraccount;
    @BeforeEach
    public void setUpServer() {
        mockServeraccount = ClientAndServer.startClientAndServer(8086);
        mockServermail = ClientAndServer.startClientAndServer(8085);
    }

    @AfterEach
    public void tearDownServer() {
        mockServeraccount.stop(); mockServermail.stop();
    }
    @Test
    public void correctTest() {
        Long tournamentID = null;
        Long userID = 1L;
        Date d = new Date(2024,01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        PermissionRequest request = new PermissionRequest(tournamentID, userID);
        ResponseEntity<Object> response = permissionController.permission(request);
        assertTrue(response.getBody().equals("Permission inserted"));
    }
    @Test
    public void TournamentAlreadyEndedTest() {
        Long userID = 1L;
        Date d = new Date(2024,01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(false);
        tournamentRepo.save(t);
        Long tournamentID = t.getTournamentID();
        PermissionRequest request = new PermissionRequest(tournamentID, userID);
        ResponseEntity<Object> response = permissionController.permission(request);
        assertTrue(response.getBody().equals("Tournament already ended"));
    }
    @Test
    public void TournamentNotExistsTest() {
        Long userID = 1L;
        Long tournamentID = 1L;
        PermissionRequest request = new PermissionRequest(tournamentID, userID);
        ResponseEntity<Object> response = permissionController.permission(request);
        assertTrue(response.getBody().equals("Invalid tournament id request"));
    }
    @Test
    public void WrongUser() throws JsonProcessingException {
        User user = new User();
        user.setEmail("xxxxx");
        user.setRole(Role.STUDENT);
        user.setId(1L);
        String userJson = new ObjectMapper().writeValueAsString(user);
        mockServeraccount
                .when(request().withMethod("POST").withPath("/api/account/user"))
                .respond(response().withStatusCode(200).withBody(userJson));
        mockServermail
                .when(request().withMethod("POST").withPath("/api/mail/direct"))
                .respond(response().withStatusCode(200).withBody("OK"));
        Long tournamentID = 98L;
        PermissionRequest request = new PermissionRequest(tournamentID, 1L);
        ResponseEntity<Object> response = permissionController.permission(request);
        assertTrue(response.getBody().equals("Invalid Request"));
    }

    @Test
    public void RightUser() throws JsonProcessingException {
        User user = new User();
        user.setEmail("xxxxx");
        user.setRole(Role.EDUCATOR);
        user.setId(1L);
        String userJson = new ObjectMapper().writeValueAsString(user);
        mockServeraccount
                .when(request().withMethod("POST").withPath("/api/account/user"))
                .respond(response().withStatusCode(200).withBody(userJson));
        mockServermail
                .when(request().withMethod("POST").withPath("/api/mail/direct"))
                .respond(response().withStatusCode(200).withBody("OK"));
        Long tournamentID = 98L;
        PermissionRequest request = new PermissionRequest(tournamentID, 1L);
        ResponseEntity<Object> response = permissionController.permission(request);
        assertTrue(response.getBody().equals("Permission inserted"));
    }
}
