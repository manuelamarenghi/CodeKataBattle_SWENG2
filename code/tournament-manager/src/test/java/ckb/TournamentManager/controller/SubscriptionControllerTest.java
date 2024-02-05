package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.in.SubscriptionRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.repo.TournamentRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest

public class SubscriptionControllerTest {
    @Autowired
    private SubscriptionController subscriptionController;

    @Autowired
    private TournamentRepo tournamentRepo;

    @Test
    public void CorrectSubscriptionTest() {
        Long tournamentID;
        Date d = new Date(2024,01,20);
        Long userID = 1L;
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        SubscriptionRequest request = new SubscriptionRequest(tournamentID, userID);
        ResponseEntity<Object> response = subscriptionController.subscription(request);
        assertEquals("Subscription inserted", response.getBody());
        tournamentRepo.deleteById(tournamentID);
    }
    @Test
    public void InvalidUserIDTest() {
        Long tournamentID;
        Date d = new Date(2024,01,20);
        Long userID = null;
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        SubscriptionRequest request = new SubscriptionRequest(tournamentID, userID);
        ResponseEntity<Object> response = subscriptionController.subscription(request);
        assertEquals("Invalid user id request", response.getBody());
        tournamentRepo.deleteById(tournamentID);
    }
    @Test
    public void InvalidTournamentIDTest() {
        Long tournamentID = null;
        Date d = new Date(2024,01,20);
        Long userID = 1L;
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        SubscriptionRequest request = new SubscriptionRequest(tournamentID, userID);
        ResponseEntity<Object> response = subscriptionController.subscription(request);
        assertEquals("Invalid tournament id request", response.getBody());
        tournamentRepo.deleteById(t.getTournamentID());
    }
    @Test
    public void RegDeadlineExpiredTest() {
        Long tournamentID;
        Date d = new java.util.Date((2019-1900),01,20);
        Long userID = 1L;
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        SubscriptionRequest request = new SubscriptionRequest(tournamentID, userID);
        ResponseEntity<Object> response = subscriptionController.subscription(request);
        assertEquals("Reg deadline expired request", response.getBody());
        tournamentRepo.deleteById(tournamentID);
    }
    @Test
    public void StatusExpiredTest() {
        Long tournamentID;
        Date d = new Date(2019,01,20);
        Long userID = 1L;
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(false);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        SubscriptionRequest request = new SubscriptionRequest(tournamentID, userID);
        ResponseEntity<Object> response = subscriptionController.subscription(request);
        assertEquals("Tournament already ended", response.getBody());
        tournamentRepo.deleteById(tournamentID);
    }
}
