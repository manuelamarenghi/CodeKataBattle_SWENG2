package ckb.TournamentManager;

import ckb.TournamentManager.controller.PermissionController;
import ckb.TournamentManager.controller.SubscriptionController;
import ckb.TournamentManager.dto.PermissionRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.repo.TournamentRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PermissionControllerTest {
    @Autowired
    private PermissionController permissionController;
    @Autowired
    private TournamentRepo tournamentRepo;

    @Test
    public void correctTest() {
        Long tournamentID = null;
        Long userID = 1L;
        Date d = new Date(2024,01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        PermissionRequest request = new PermissionRequest(tournamentID, userID);
        ResponseEntity<Object> response = permissionController.permission(request);
        assertTrue(response.getBody().equals("Permission inserted"));
    }
    @Test
    public void TournamentAlreadyEndedTest() {
        Long userID = 1L;
        Date d = new Date(2024,01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(false);
        tournamentRepo.save(t);
        Long tournamentID = t.getTournamentID();
        PermissionRequest request = new PermissionRequest(tournamentID, userID);
        ResponseEntity<Object> response = permissionController.permission(request);
        assertTrue(response.getBody().equals("Tournament already ended"));
    }
    @Test
    public void TournamentNotExistsTest() {
        Long userID = 1L;
        Long tournamentID = 1L;
        PermissionRequest request = new PermissionRequest(tournamentID, userID);
        ResponseEntity<Object> response = permissionController.permission(request);
        assertTrue(response.getBody().equals("Invalid tournament id request"));
    }
}
