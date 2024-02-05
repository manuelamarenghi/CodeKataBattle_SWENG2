package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.in.NewTournamentRequest;
import ckb.TournamentManager.repo.TournamentRepo;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewTournamentControllerTest {
    @Autowired
    private NewTournamentController newTournamentController;

    @Autowired
    private TournamentRepo tournamentRepo;

    private static ClientAndServer mockServerMailService;
    private static ClientAndServer mockServerAccountManagerService;


    @BeforeAll
    public void startServer() {
        newTournamentController.initTestMode();
    }

    @BeforeEach
    public void setUpServer() {
        mockServerMailService = ClientAndServer.startClientAndServer(8085);
        mockServerAccountManagerService = ClientAndServer.startClientAndServer(8086);
    }

    @AfterEach
    public void tearDownServer() {
        tournamentRepo.deleteAll();
        mockServerMailService.stop();
        mockServerAccountManagerService.stop();
    }

    private void setMockServers() throws JSONException {
        mockServerMailService
                .when(request().withMethod("POST").withPath("/api/mail/all-students"))
                .respond(response().withStatusCode(200).withBody("ok!"));
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

    @Test
    public void correctRequestTest() throws JSONException {
        setMockServers();

        Date d = new Date((2024 - 1900), Calendar.FEBRUARY, 20);
        Long creatorID = 1L;
        NewTournamentRequest request = new NewTournamentRequest(creatorID, "Tournament 1", d);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
    }

    @Test
    public void invalidDeadlineTest() throws JSONException {
        setMockServers();

        Date d = new Date((2020 - 1900), Calendar.FEBRUARY, 20);
        Long creatorID = 1L;
        NewTournamentRequest request = new NewTournamentRequest(creatorID, "Tournament 1", d);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void nullDeadlineTest() throws JSONException {
        setMockServers();

        Date d = null;
        Long creatorID = 1L;
        NewTournamentRequest request = new NewTournamentRequest(creatorID, "Tournament 1", d);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void sendEmailCorrectly() throws JSONException {
        setMockServers();

        Date d = new Date((2024 - 1900), Calendar.DECEMBER, 20);
        Long creatorID = 1L;
        NewTournamentRequest request = new NewTournamentRequest(creatorID, "Tournament 1", d);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void nullCreatorID() throws JSONException {
        setMockServers();

        Date d = new Date((2025 - 1900), Calendar.FEBRUARY, 20);
        Long creatorID = null;
        NewTournamentRequest request = new NewTournamentRequest(creatorID, "Tournament 1", d);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void sameNameTournament() throws JSONException {
        setMockServers();

        Date d = new Date((2024 - 1900), Calendar.FEBRUARY, 20);
        Long creatorID = 1L;
        NewTournamentRequest request = new NewTournamentRequest(creatorID, "Tournament 1", d);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        d = new Date((2024 - 1900), Calendar.FEBRUARY, 20);
        request = new NewTournamentRequest(creatorID, "Tournament 1", d);
        response = newTournamentController.newTournament(request);
        assertTrue(response.getStatusCode().is4xxClientError());
    }
}
