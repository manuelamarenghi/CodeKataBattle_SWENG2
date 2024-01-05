package ckb.TournamentManager;

import ckb.TournamentManager.controller.GetTournamentPageController;
import ckb.TournamentManager.dto.incoming.NewTournamentRequest;
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
        Long tournamentID = 90L;
        mockServer
                .when(request().withMethod("GET").withPath("/api/battle/nome")
                        .withQueryStringParameter("tournamentID", String.valueOf(tournamentID)))
                .respond(response().withStatusCode(200).withBody(json));
        NewTournamentRequest.GetTournamentPageRequest request = new NewTournamentRequest.GetTournamentPageRequest(tournamentID);
        /*System.out.println(tournamentRankingRepo.findDistinctByTournamentID(tournamentID));
        System.out.println(tournamentRankingRepo.findAllByTournamentIDOrderByScoreAsc(tournamentID));*/
        ResponseEntity<Object> response = getTournamentController.getTournamentPage(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());

    }
    @Test
    public void testRepo(){
        System.out.println(tournamentRankingRepo.findAllByTournamentIDOrderByScoreDesc(97L));
    }

}
