package ckb.TournamentManager.repo;

import ckb.TournamentManager.model.TournamentRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRankingRepo extends JpaRepository<TournamentRanking, Long> {

    Optional<TournamentRanking> findByTournamentID(Long tournamentID);

    Optional<TournamentRanking> findByTournamentIDAndUserID(Long tournamentID, Long userID);

    List<TournamentRanking> findAllByTournamentID(Long tournamentID);

    Optional<List<TournamentRanking>> findDistinctByTournamentID(Long tournamentID);

    void deleteByTournamentID(Long tournamentID);

    List<TournamentRanking> findByTournamentIDOrderByScoreAsc(Long tournamentID);

    List<TournamentRanking> findAllByTournamentIDOrderByScoreAsc(Long tournamentID);
    List<TournamentRanking> findAllByTournamentIDOrderByScoreDesc(Long tournamentID);
}
