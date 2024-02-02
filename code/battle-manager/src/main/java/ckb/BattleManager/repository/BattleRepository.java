package ckb.BattleManager.repository;

import ckb.BattleManager.model.Battle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BattleRepository extends JpaRepository<Battle, Long> {
    // I can extend with my methods
    // @Query("SELECT s FROM Student s WHERE s.email = ?1")
    // Optional<Student> findStudentByEmail(String email)
    // SELECT * student WHERE email = ...

    List<Battle> findBattlesByTournamentId(Long idTournament);

    @Query("select b from Battle b where b.hasStarted = false and b.regDeadline < ?1")
    List<Battle> findBattlesByHasStartedIsFalseAndRegDeadlineIsBefore(LocalDateTime now);

    @Query("select b from Battle b where b.hasEnded = false and b.subDeadline < ?1")
    List<Battle> findBattlesByHasEndedIsFalseAndSubDeadlineIsBefore(LocalDateTime now);
}
