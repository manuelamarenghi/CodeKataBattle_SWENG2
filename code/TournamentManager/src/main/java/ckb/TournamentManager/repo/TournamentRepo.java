package ckb.TournamentManager.repo;

import ckb.TournamentManager.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepo extends JpaRepository<Tournament,Long> {

    @Override
    Optional<Tournament> findById(@Param("tournamentID")Long tournamentID);
    Optional<Tournament> findByTournamentID(@Param("tournamentID") Long tournamentID);
    @Override
    void delete(Tournament entity);
    @Override
    void deleteById(@Param("tournamentID")Long tournamentID);
    @Override
    <S extends Tournament> S save(S entity);
    @Override
    <S extends Tournament> List<S> saveAll(Iterable<S> entities);

}
