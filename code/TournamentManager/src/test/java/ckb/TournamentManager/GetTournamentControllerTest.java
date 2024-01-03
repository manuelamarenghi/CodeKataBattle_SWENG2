package ckb.TournamentManager;

import ckb.TournamentManager.controller.GetTournamentPageController;
import ckb.TournamentManager.dto.GetTournamentPageRequest;
import ckb.TournamentManager.model.Tournament;
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
public class GetTournamentControllerTest {
    @Autowired
    private GetTournamentPageController getTournamentController;
    @Autowired
    private TournamentRepo tournamentRepo;
    private static ClientAndServer mockServer;

    @BeforeEach
    public void setUpServer() {
        mockServer = ClientAndServer.startClientAndServer(8085);
    }

    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }

    @Test
    public void correctRequestTest() {
        mockServer
                .when(request().withMethod("POST").withPath("/api/mail/all-students"))
                .respond(response().withStatusCode(200).withBody("ok!"));
        Date d = new Date((2024-1900),01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        Long tournamentID = t.getTournamentID();
        GetTournamentPageRequest request = new GetTournamentPageRequest(tournamentID);
        ResponseEntity<Object> response = getTournamentController.getTournamentPage(request);
    }
}
