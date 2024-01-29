package ckb.TournamentManager;

import ckb.TournamentManager.controller.CloseTournamentController;
import ckb.TournamentManager.dto.incoming.CloseTournamentRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.repo.PermissionRepo;
import ckb.TournamentManager.repo.TournamentRepo;
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
public class CloseTournamentTest {
    @Autowired
    private CloseTournamentController closetController;
    @Autowired
    private TournamentRepo tournamentRepo;
    @Autowired
    private PermissionRepo permissionRepo;
    private static ClientAndServer mockServer;
    @BeforeEach
    public void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(8082);
    }
    @AfterEach
    public void stopMockServer() {
        mockServer.stop();
    }
    @Test
    public void correctTest() {
        boolean b = true;
        CloseTournamentRequest request;
        mockServer.when(request()
                        .withMethod("POST")
                        .withPath("/api/battle/servizio"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(String.valueOf(b)));
        Long tournamentID = null;
        Date d = new Date((2024 - 1900), 01, 20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        t.setCreatorID(7L);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        Long creatorID = 7L;
        request = new CloseTournamentRequest(tournamentID,creatorID);
        ResponseEntity<Object> response = closetController.CloseTournament(request);
        assertTrue(response.getBody().equals("Tournament closed"));
        tournamentRepo.deleteById(tournamentID);
    }
    @Test
    public void cannotcloseTest() {
        boolean b = false;
        CloseTournamentRequest request;
        mockServer.when(request()
                        .withMethod("POST")
                        .withPath("/api/battle/servizio"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(String.valueOf(b)));
        Long tournamentID = null;
        Date d = new Date((2024 - 1900), 01, 20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        t.setCreatorID(7L);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        Long creatorID = 7L;
        request = new CloseTournamentRequest(tournamentID,creatorID);
        ResponseEntity<Object> response = closetController.CloseTournament(request);
        assertTrue(response.getBody().equals("Not possible to close"));
        tournamentRepo.deleteById(tournamentID);
    }
    @Test
    public void TournamentNullTest() {
        Date d = new Date((2024-1900),01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(false);
        t.setCreatorID(7L);
        tournamentRepo.save(t);
        Long tournamentID = null;
        Long creatorID = 7L;
        CloseTournamentRequest request = new CloseTournamentRequest(tournamentID,creatorID);
        ResponseEntity<Object> response = closetController.CloseTournament(request);
        assertTrue(response.getBody().equals("Invalid tournament id request"));
    }
    @Test
    public void TournamentNotExistsTest() {
        Date d = new Date((2024-1900),01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(false);
        t.setCreatorID(7L);
        tournamentRepo.save(t);
        Long tournamentID = 4L;
        Long creatorID = 7L;
        CloseTournamentRequest request = new CloseTournamentRequest(tournamentID,creatorID);
        ResponseEntity<Object> response = closetController.CloseTournament(request);
        assertTrue(response.getBody().equals("Invalid tournament id request"));
    }
    @Test
    public void WronIDCreator(){
        boolean b = true;
        CloseTournamentRequest request;
        mockServer.when(request()
                        .withMethod("POST")
                        .withPath("/api/battle/servizio"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(String.valueOf(b)));
        Long tournamentID = null;
        Date d = new Date((2024 - 1900), 01, 20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        t.setCreatorID(8L);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        Long creatorID = 7L;
        request = new CloseTournamentRequest(tournamentID,creatorID);
        ResponseEntity<Object> response = closetController.CloseTournament(request);
        assertTrue(response.getBody().equals("Not allowed to close tournament"));
        tournamentRepo.deleteById(tournamentID);
    }
}
