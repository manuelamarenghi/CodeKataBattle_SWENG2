package ckb.BattleManager.repository;

import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {
    // get the team of a student with his/her id and the battle_id
    @Query("SELECT t FROM Participation p JOIN Team t ON p.participationId.team = t" +
            " WHERE p.participationId.studentId = :idStudent AND t.battle.battleId = :idBattle")
    Optional<Team> findTeamByBattleIdAndStudentId(@Param("idStudent") Long idStudent, @Param("idBattle") Long idBattle);

    boolean existsParticipationByParticipationId_Team(Team team);
}
