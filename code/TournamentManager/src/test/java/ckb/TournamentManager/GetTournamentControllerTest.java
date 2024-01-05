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
        /*Tournament t = new Tournament();
        t.setRegdeadline(new Date((2024-1900),01,20));
        t.setStatus(true);
        tournamentRepo.save(t);*/
        //setUpTouRank(90L);
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
        Long tournamentID = 90L;
        mockServer
                .when(request().withMethod("GET").withPath("/api/battle/nome")
                        .withQueryStringParameter("tournamentID", String.valueOf(tournamentID)))
                .respond(response().withStatusCode(200).withBody(json));
        GetTournamentPageRequest request = new GetTournamentPageRequest(tournamentID);
        System.out.println(tournamentRankingRepo.findDistinctByTournamentID(tournamentID));
        System.out.println(tournamentRankingRepo.findAllByTournamentIDOrderByScoreAsc(tournamentID));
        /*   ResponseEntity<Object> response = getTournamentController.getTournamentPage(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        tournamentRankingRepo.delete(x);
        tournamentRankingRepo.delete(y);
        tournamentRepo.delete(t);*/
    }
    void setUpTouRank(Long tournamentID){
        try {
            TournamentRanking x = new TournamentRanking();
            x.setTournamentID(tournamentID);
            x.setUserID(494L);
            x.setScore(3);
            tournamentRankingRepo.save(x);
        } catch (DataIntegrityViolationException ignored) {
        }
        try{
            TournamentRanking y = new TournamentRanking();
            y.setTournamentID(tournamentID);
            y.setUserID(496L);
            y.setScore(2);
            tournamentRankingRepo.save(y);
        } catch (DataIntegrityViolationException ignored) {
        }
        try{
            TournamentRanking z = new TournamentRanking();
            z.setTournamentID(tournamentID);
            z.setUserID(498L);
            z.setScore(12);
            tournamentRankingRepo.save(z);
        } catch (DataIntegrityViolationException ignored) {
        }
    }
}
