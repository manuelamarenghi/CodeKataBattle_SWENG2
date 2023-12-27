package ckb.TournamentManager.repo;

import ckb.TournamentManager.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TournamentRepo extends JpaRepository<Tournament,Long> {


}
