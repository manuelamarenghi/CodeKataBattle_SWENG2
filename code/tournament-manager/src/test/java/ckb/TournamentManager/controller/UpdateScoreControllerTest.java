package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.UpdateScoreRequest;
import ckb.TournamentManager.model.TournamentRanking;
import ckb.TournamentManager.model.WorkingPair;
import ckb.TournamentManager.repo.TournamentRankingRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UpdateScoreControllerTest {
    @Autowired
    private UpdateScoreController updateController;

    @Autowired
    private TournamentRankingRepo tournamentRankingRepo;

    @Test
    public void correctTest() {
        TournamentRanking tr = new TournamentRanking();
        tr.setTournamentID(1L);
        tr.setUserID(1L);
        tr.setScore(0);
        tournamentRankingRepo.save(tr);

        TournamentRanking tr2 = new TournamentRanking();
        tr2.setTournamentID(1L);
        tr2.setUserID(2L);
        tr2.setScore(4);
        tournamentRankingRepo.save(tr2);

        List<WorkingPair<Long, Integer>> scores = List.of(
                new WorkingPair<>(1L, 1),
                new WorkingPair<>(2L, 3)
        );

        UpdateScoreRequest request = new UpdateScoreRequest(1L, scores);
        ResponseEntity<Object> response = updateController.updateScore(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}
