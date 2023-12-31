package ckb.TournamentManager;

import ckb.TournamentManager.controller.SubscriptionController;
import ckb.TournamentManager.dto.SubscriptionRequest;
import ckb.TournamentManager.repo.TournamentRepo;
import ckb.TournamentManager.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class SubscriptionControllerTest {
    @Autowired
    private SubscriptionController subscriptionController;
    @Autowired
    private TournamentRepo tournamentRepo;

    @Test
    public void CorrectSubscriptionTest() {
        Long tournamentId = 1L;
        Long userId = 1L;
        SubscriptionRequest request = new SubscriptionRequest(tournamentId, userId);
        subscriptionController.subscription(request);

    }
}
