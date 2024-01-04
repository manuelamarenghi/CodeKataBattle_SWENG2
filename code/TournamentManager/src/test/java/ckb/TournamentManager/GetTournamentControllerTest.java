package ckb.TournamentManager;

import ckb.TournamentManager.controller.GetTournamentPageController;
import ckb.TournamentManager.controller.ResponseWrapper;
import ckb.TournamentManager.dto.GetTournamentPageRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.model.TournamentRanking;
import ckb.TournamentManager.repo.TournamentRankingRepo;
import ckb.TournamentManager.repo.TournamentRepo;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
        Date d = new Date((2024-1900),01,20);
        /*Tournament t = new Tournament();
        TournamentRanking x = new TournamentRanking();
        TournamentRanking y = new TournamentRanking();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        x.setTournamentID(89);
        x.setScore(3);
        x.setUserID(495L);
        y.setTournamentID(t.getTournamentID());
        y.setScore(2);
        y.setUserID(496L);
        try {
            tournamentRankingRepo.save(x);
        } catch (DataIntegrityViolationException e) {

        }
        try {
            tournamentRankingRepo.save(y);
        } catch (DataIntegrityViolationException e) {

        }*/
        Long tournamentID = 89L;
        TournamentRanking y = new TournamentRanking();
        y.setTournamentID(89L);
        y.setScore(3);
        y.setUserID(498L);
        tournamentRankingRepo.save(y);
        mockServer
                .when(request().withMethod("GET").withPath("/api/battle/nome")
                        .withQueryStringParameter("tournamentID", String.valueOf(tournamentID)))
                .respond(response().withStatusCode(200).withBody(json));
        GetTournamentPageRequest request = new GetTournamentPageRequest(tournamentID);
        System.out.println(tournamentRankingRepo.findAllByTournamentIDOrderByScoreAsc(request.getTournamentID()));
        /*   ResponseEntity<Object> response = getTournamentController.getTournamentPage(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        tournamentRankingRepo.delete(x);
        tournamentRankingRepo.delete(y);
        tournamentRepo.delete(t);*/
    }
}
