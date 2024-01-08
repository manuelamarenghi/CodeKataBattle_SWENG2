package ckb.BattleManager.repository;

import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {
    // get the team of a student with his/her id and the battle_id
    @Query("SELECT t FROM Participation p JOIN Team t ON p.participationId.teamId = t.teamId" +
            " WHERE p.participationId.studentId = :studentId AND t.battle.battleId = :battleId")
    Team findTeamByBattleIdAndStudentId(@Param("studentId") Long studentId, @Param("battleId") Long battleId);

    boolean existsParticipationByParticipationId_TeamId(Long teamId);
}
