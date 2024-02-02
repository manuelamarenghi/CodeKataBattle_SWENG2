package ckb.BattleManager.repository;

import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Optional<Participation> findByStudentIdAndTeam(Long studentId, Team team);
}
