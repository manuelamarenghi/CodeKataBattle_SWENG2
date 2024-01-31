package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.GetAllTournamentsRequest;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.repo.TournamentRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Date;

@SpringBootTest
public class GetAllTournamentsTest {
    @Autowired
    private GetAllTournamentsController getTournamentsController;
    @Autowired
    private TournamentRepo tournamentRepo;

    @Test
    public void correctRequestTest() {
         Tournament tournament = new Tournament();
         Date d = new Date((2024 - 1900), 03, 20);
         tournament.setRegdeadline(d);
         tournament.setStatus(true);
        tournament.setName("tournament");
         tournamentRepo.save(tournament);
        GetAllTournamentsRequest request = new GetAllTournamentsRequest("tournament");
        ResponseEntity<Object> response =getTournamentsController.getTournaments(request);

        System.out.println(response.getBody());
    }
    @Test
    public void noTournamentTest() {
        GetAllTournamentsRequest request = new GetAllTournamentsRequest("tournament");
        ResponseEntity<Object> response =getTournamentsController.getTournaments(request);
        System.out.println(response.getBody());
    }
}
