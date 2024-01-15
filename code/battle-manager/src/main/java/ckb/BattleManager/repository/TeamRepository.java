package ckb.BattleManager.repository;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import org.apache.commons.lang3.tuple.Pair;
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

    @Query("select t from Team t join Participation p on t.teamId = p.participationId.team.teamId " +
            "where t.battle = :battle and p.participationId.studentId = :idStudent")
    Optional<Team> findTeamByBattleAndParticipationId_TeamId(@Param("battle") Battle battle,
                                                             @Param("idStudent") Long idStudent);

    @Query("select p.participationId.studentId, t.score " +
            "from Team t join Participation p on t.teamId = p.participationId.team.teamId " +
            "where t.battle = :battle")
    List<Pair<Long, Long>> findPairsIdUserPointsByBattleId(@Param("battle") Battle battle);

}
