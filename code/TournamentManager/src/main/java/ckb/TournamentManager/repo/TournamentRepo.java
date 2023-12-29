package ckb.TournamentManager.repo;

import ckb.TournamentManager.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TournamentRepo extends JpaRepository<Tournament,Long> {

    @Override
    Optional<Tournament> findById(Long aLong);


}
