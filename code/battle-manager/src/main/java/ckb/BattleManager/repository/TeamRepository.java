package ckb.BattleManager.repository;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findTeamsByBattle(Battle battle);
}
