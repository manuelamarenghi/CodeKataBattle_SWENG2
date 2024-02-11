package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.in.GetTournamentPageRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.model.TournamentRanking;
import ckb.TournamentManager.repo.TournamentRankingRepo;
import ckb.TournamentManager.repo.TournamentRepo;
import org.json.JSONArray;
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
        getTournamentController.initTestMode();
        mockServer = ClientAndServer.startClientAndServer(8082);
    }

    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }

    @Test
    public void correctRequestTest() throws JSONException {
        Long a = 48593L;
        Long b = 48503L;
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(a);
        jsonArray.put(b);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("battlesID", jsonArray);

        Tournament t = new Tournament();
        t.setRegdeadline(new Date((2024 - 1900), Calendar.AUGUST, 20));
        t.setStatus(true);
        tournamentRepo.save(t);
        Long tournamentID = t.getTournamentID();
        mockServer
                .when(request().withMethod("POST").withPath("/api/battle/get-battles-tournament"))
                .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_JSON).withBody(jsonObject.toString()));
        GetTournamentPageRequest request = new GetTournamentPageRequest(tournamentID);

        ResponseEntity<ResponseWrapper> response = getTournamentController.getTournamentPage(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        System.out.println(response.getBody());
        tournamentRepo.deleteById(tournamentID);
    }

    @Test
    public void noBattleAnswer() throws JSONException {
        TournamentRanking tr = new TournamentRanking();
        TournamentRanking tr2 = new TournamentRanking();
        tr.setScore(4);
        tr.setUserID(1L);
        tr2.setScore(3);
        tr2.setUserID(2L);
        Tournament t = new Tournament();
        t.setRegdeadline(new Date((2024 - 1900), Calendar.AUGUST, 20));
        t.setStatus(true);
        tournamentRepo.save(t);
        tr.setTournamentID(t.getTournamentID());
        tr2.setTournamentID(t.getTournamentID());
        tournamentRankingRepo.save(tr);
        tournamentRankingRepo.save(tr2);
        Long tournamentID = t.getTournamentID();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("battlesID", new JSONArray());

        mockServer
                .when(request().withMethod("POST").withPath("/api/battle/get-battles-tournament"))
                .respond(response().withStatusCode(200).withContentType(MediaType.APPLICATION_JSON).
                        withBody(jsonObject.toString()));

        GetTournamentPageRequest request = new GetTournamentPageRequest(tournamentID);
        ResponseEntity<ResponseWrapper> response = getTournamentController.getTournamentPage(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        System.out.println(response.getBody());
        tournamentRepo.deleteById(tournamentID);
        tournamentRankingRepo.delete(tr);
        tournamentRankingRepo.delete(tr2);
    }
}
