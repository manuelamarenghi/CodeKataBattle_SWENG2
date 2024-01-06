package ckb.BattleManager.repository;

import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {
}
