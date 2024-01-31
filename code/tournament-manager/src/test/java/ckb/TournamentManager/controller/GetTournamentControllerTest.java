package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.GetTournamentPageRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.model.TournamentRanking;
import ckb.TournamentManager.repo.TournamentRankingRepo;
import ckb.TournamentManager.repo.TournamentRepo;
import org.json.JSONArray;
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
    @Autowired
    private TournamentRankingRepo tournamentRankingRepo;
    private static ClientAndServer mockServer;

    @BeforeEach
    public void setUpServer() {
        mockServer = ClientAndServer.startClientAndServer(8082);
    }

    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }

    @Test
    public void correctRequestTest() {
        Long a = Long.valueOf(48593);
        Long b = Long.valueOf(48503);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(a);
        jsonArray.put(b);
        String json = jsonArray.toString();
        Tournament t = new Tournament();
        t.setRegdeadline(new Date((2024-1900),07,20));
        t.setStatus(true);
        tournamentRepo.save(t);
        Long tournamentID = t.getTournamentID();
        mockServer
                .when(request().withMethod("POST").withPath("/api/battle/get-battles-tournament"))
                .respond(response().withStatusCode(200).withBody(json));
        GetTournamentPageRequest request = new GetTournamentPageRequest(tournamentID);
        ResponseEntity<Object> response = getTournamentController.getTournamentPage(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        System.out.println(response.getBody());
        tournamentRepo.deleteById(tournamentID);
    }
    @Test
    public void noBattleAnswer(){
        TournamentRanking tr = new TournamentRanking();
        TournamentRanking tr2 = new TournamentRanking();
        tr.setScore(4);
        tr.setUserID(1L);
        tr2.setScore(3);
        tr2.setUserID(2L);
        Tournament t = new Tournament();
        t.setRegdeadline(new Date((2024-1900),07,20));
        t.setStatus(true);
        tournamentRepo.save(t);
        tr.setTournamentID(t.getTournamentID());
        tr2.setTournamentID(t.getTournamentID());
        tournamentRankingRepo.save(tr);
        tournamentRankingRepo.save(tr2);
        Long tournamentID = t.getTournamentID();
        mockServer
                .when(request().withMethod("POST").withPath("/api/battle/get-battles-tournament"))
                .respond(response().withStatusCode(200));
        GetTournamentPageRequest request = new GetTournamentPageRequest(tournamentID);
        ResponseEntity<Object> response = getTournamentController.getTournamentPage(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        System.out.println(response.getBody());
        tournamentRepo.deleteById(tournamentID);
        tournamentRankingRepo.delete(tr);
        tournamentRankingRepo.delete(tr2);
    }
    @Test
    public void noSubscribedAnswer(){
        Long a = Long.valueOf(48593);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(a);
        String json = jsonArray.toString();
        Tournament t = new Tournament();
        t.setRegdeadline(new Date((2024-1900),07,20));
        t.setStatus(true);
        tournamentRepo.save(t);
        Long tournamentID = t.getTournamentID();
        mockServer
                .when(request().withMethod("POST").withPath("/api/battle/get-battles-tournament"))
                .respond(response().withStatusCode(200).withBody(json));
        GetTournamentPageRequest request = new GetTournamentPageRequest(tournamentID);
        ResponseEntity<Object> response = getTournamentController.getTournamentPage(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        System.out.println(response.getBody());
        tournamentRepo.deleteById(tournamentID);
    }
}
