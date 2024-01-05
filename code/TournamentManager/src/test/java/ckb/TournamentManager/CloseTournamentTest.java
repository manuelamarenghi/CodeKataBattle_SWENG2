package ckb.TournamentManager;

import ckb.TournamentManager.controller.CloseTournamentController;
import ckb.TournamentManager.dto.incoming.CloseTournamentRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.repo.TournamentRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CloseTournamentTest {
    @Autowired
    private CloseTournamentController closetController;
    @Autowired
    private TournamentRepo tournamentRepo;

    @Test
    public void correctTest() {
        Long tournamentID = null;
        Date d = new Date((2024-1900),01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(true);
        tournamentRepo.save(t);
        tournamentID = t.getTournamentID();
        CloseTournamentRequest request = new CloseTournamentRequest(tournamentID);
        ResponseEntity<Object> response = closetController.CloseTournament(request);
        assertTrue(response.getBody().equals("Tournament closed"));
    }
    @Test
    public void TournamentNullTest() {
        Date d = new Date((2024-1900),01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(false);
        tournamentRepo.save(t);
        Long tournamentID = null;
        CloseTournamentRequest request = new CloseTournamentRequest(tournamentID);
        ResponseEntity<Object> response = closetController.CloseTournament(request);
        assertTrue(response.getBody().equals("Invalid tournament id request"));
    }
    @Test
    public void TournamentNotExistsTest() {
        Date d = new Date((2024-1900),01,20);
        Tournament t = new Tournament();
        t.setRegdeadline(d);
        t.setStatus(false);
        tournamentRepo.save(t);
        Long tournamentID = 1L;
        CloseTournamentRequest request = new CloseTournamentRequest(tournamentID);
        ResponseEntity<Object> response = closetController.CloseTournament(request);
        assertTrue(response.getBody().equals("Invalid tournament id request"));
    }
}
