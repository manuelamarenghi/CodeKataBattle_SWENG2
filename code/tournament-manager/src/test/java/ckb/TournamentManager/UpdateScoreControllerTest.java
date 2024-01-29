package ckb.TournamentManager;

import ckb.TournamentManager.controller.UpdateScoreController;
import ckb.TournamentManager.dto.incoming.UpdateScoreRequest;
import ckb.TournamentManager.model.TournamentRanking;
import ckb.TournamentManager.repo.TournamentRankingRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UpdateScoreControllerTest {
    @Autowired
    private UpdateScoreController updateController;

    @Autowired
    private TournamentRankingRepo tournamentrankRepo;

    @Test
    public void correctTest(){
        TournamentRanking tr = new TournamentRanking();
        tr.setTournamentID(1L);
        tr.setUserID(1L);
        tr.setScore(0);
        tournamentrankRepo.save(tr);
        TournamentRanking tr2 = new TournamentRanking();
        tr2.setTournamentID(1L);
        tr2.setUserID(2L);
        tr2.setScore(4);
        tournamentrankRepo.save(tr2);
        HashMap<Long,Integer> scores = new HashMap<>();
        scores.put(1L,1);
        scores.put(2L,3);
        UpdateScoreRequest request = new UpdateScoreRequest(1L,scores);
        ResponseEntity<Object> response = updateController.UpdateScore(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}
