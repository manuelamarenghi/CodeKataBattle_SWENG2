package ckb.BattleManager.repository;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findTeamsByBattle(Battle battle);

    boolean existsByBattle(Battle battle);

    @Query("select t.battle from Team t where t.teamId = :idTeam")
    Optional<Battle> findBattleByTeamId(@Param("idTeam") Long idTeam);

    @Query("select t from Team t join Participation p on t.teamId = p.participationId.teamId.teamId " +
            "where t.battle = :battle and p.participationId.studentId = :idStudent")
    Optional<Team> findTeamByBattleAndParticipationId_TeamId(@Param("battle") Battle battle,
                                                             @Param("idStudent") Long idStudent);

}
