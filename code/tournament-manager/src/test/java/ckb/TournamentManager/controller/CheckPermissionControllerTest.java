package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.CheckPermissionRequest;
import ckb.TournamentManager.dto.incoming.NewTournamentRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.repo.PermissionRepo;
import ckb.TournamentManager.repo.TournamentRepo;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
class CheckPermissionControllerTest {

    @Autowired
    private TournamentRepo tournamentRepo;

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private CheckPermissionController checkPermissionController;

    @Autowired
    private NewTournamentController newTournamentController;

    private static ClientAndServer mockServerMailService;
    private static ClientAndServer mockServerAccountManagerService;

    @BeforeEach
    public void setUpServer() throws JSONException {
        newTournamentController.initTestMode();
        mockServerMailService = ClientAndServer.startClientAndServer(8085);
        mockServerAccountManagerService = ClientAndServer.startClientAndServer(8086);

        mockServerMailService
                .when(request().withMethod("POST").withPath("/api/mail/all-students"))
                .respond(response().withStatusCode(200));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        jsonObject.put("email", "example@example.com");
        jsonObject.put("fullName", "John Doe");
        jsonObject.put("password", "password123");
        jsonObject.put("role", "EDUCATOR");
        mockServerAccountManagerService
                .when(request().withMethod("POST").withPath("/api/account/user"))
                .respond(response().withStatusCode(200).withBody(
                        jsonObject.toString()
                ).withContentType(MediaType.APPLICATION_JSON));
    }

    @AfterEach
    public void tearDown() {
        mockServerMailService.stop();
        mockServerAccountManagerService.stop();
        tournamentRepo.deleteAll();
        permissionRepo.deleteAll();
    }

    @Test
    void checkPermissionFalse() {
        ResponseEntity<Object> response = newTournamentController.newTournament(
                new NewTournamentRequest(1L, "Tournament 1", new Date(2345 - 1900, Calendar.FEBRUARY, 1))
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());

        response = checkPermissionController.checkPermission(new CheckPermissionRequest(1L, 2L));
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void checkPermissionTrue() {
        ResponseEntity<Object> responseTournament = newTournamentController.newTournament(
                new NewTournamentRequest(1L, "Tournament 1", new Date(2345 - 1900, Calendar.FEBRUARY, 1))
        );

        assertTrue(responseTournament.getStatusCode().is2xxSuccessful());
        assertNotNull(responseTournament.getBody());

        Tournament tournament = (Tournament) responseTournament.getBody();

        ResponseEntity<Object> response = checkPermissionController.checkPermission(new CheckPermissionRequest(tournament.getTournamentID(), 1L));
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}