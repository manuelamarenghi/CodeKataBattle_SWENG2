package ckb.TournamentManager;

import ckb.TournamentManager.controller.NewTournamentController;
import ckb.TournamentManager.controller.SubscriptionController;
import ckb.TournamentManager.dto.SubscriptionRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.repo.TournamentRepo;
import ckb.TournamentManager.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest

public class SubscriptionControllerTest {
    @Autowired
    private SubscriptionController subscriptionController;

    @Autowired
    private TournamentRepo tournamentRepo;

    @Test
    public void CorrectSubscriptionTest() {
        Long tournamentID = 1L;
        Date d = new Date(2024,01,20);
        Long userID = 1L;
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        SubscriptionRequest request = new SubscriptionRequest(tournamentID, userID);
        ResponseEntity<Object> response = subscriptionController.subscription(request);
        assertTrue(response.getBody().equals("Subscription inserted"));
    }
}
