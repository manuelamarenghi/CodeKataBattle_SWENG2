package ckb.TournamentManager.repo;

import ckb.TournamentManager.model.TournamentRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentRankingRepo extends JpaRepository<TournamentRanking,Long> {
    @Override
    Optional<TournamentRanking> findById(Long aLong);
    Optional<TournamentRanking> findByTournamentIDAndUserID(Long tournamentID, Long userID);
    @Override
    void delete(TournamentRanking entity);
    @Override
    void deleteById(Long aLong);
    @Override
    <S extends TournamentRanking> S save(S entity);
    @Override
    <S extends TournamentRanking> List<S> saveAll(Iterable<S> entities);

    List<TournamentRanking> findAllById(Long tournamentId);
    List<TournamentRanking> orderByScore(Long tournamentId);
}
