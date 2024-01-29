package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.NewTournamentRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NewTournamentControllerTest {
    @Autowired
    private NewTournamentController newTournamentController;

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
        mockServerMailService.stop();
        mockServerAccountManagerService.stop();
    }

    @Test
    public void correctRequestTest() throws JSONException {
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

        Date d = new Date((2024 - 1900), Calendar.FEBRUARY, 20);
        Long creatorID = 1L;
        NewTournamentRequest request = new NewTournamentRequest(d, creatorID);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);
        assertEquals("Tournament created", response.getBody());
    }

    @Test
    public void invalidDeadlineTest() throws JSONException {
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

        Date d = new Date((2020 - 1900), Calendar.FEBRUARY, 20);
        Long creatorID = 1L;
        NewTournamentRequest request = new NewTournamentRequest(d, creatorID);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);
        assertEquals("Registration deadline is in the past", response.getBody());
    }

    @Test
    public void nullDeadlineTest() throws JSONException {
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
        Date d = null;
        Long creatorID = 1L;
        NewTournamentRequest request = new NewTournamentRequest(d, creatorID);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);
        assertEquals("Registration deadline is null", response.getBody());
    }

    @Test
    public void sendEmailCorrectly() throws JSONException {
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
        Date d = new Date((2024 - 1900), Calendar.DECEMBER, 20);
        Long creatorID = 1L;
        NewTournamentRequest request = new NewTournamentRequest(d, creatorID);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);
        assertEquals("Tournament created", response.getBody());
    }

    @Test
    public void incorrectCreatorID() throws JSONException {
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
        Date d = new Date((2025 - 1900), Calendar.FEBRUARY, 20);
        Long creatorID = null;
        NewTournamentRequest request = new NewTournamentRequest(d, creatorID);
        ResponseEntity<Object> response = newTournamentController.newTournament(request);
        assertEquals("Creator ID is null", response.getBody());
    }
}
