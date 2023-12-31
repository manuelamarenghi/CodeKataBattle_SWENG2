package ckb.TournamentManager.repo;

import ckb.TournamentManager.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TournamentRepo extends JpaRepository<Tournament,Long> {

    @Override
    Optional<Tournament> findById(Long aLong);
    Optional<Tournament> findByTournamentID(Long tournamentID);
    @Override
    void delete(Tournament entity);
    @Override
    void deleteById(Long aLong);
    @Override
    <S extends Tournament> S save(S entity);
    @Override
    <S extends Tournament> List<S> saveAll(Iterable<S> entities);

}
