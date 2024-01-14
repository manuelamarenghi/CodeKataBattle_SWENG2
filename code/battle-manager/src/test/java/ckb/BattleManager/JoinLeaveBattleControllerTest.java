package ckb.BattleManager;

import ckb.BattleManager.controller.JoinLeaveBattleController;
import ckb.BattleManager.dto.input.StudentBattle;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.ParticipationRepository;
import ckb.BattleManager.repository.TeamRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JoinLeaveBattleControllerTest {
    private final JoinLeaveBattleController joinLeaveBattleController;
    private final BattleRepository battleRepository;
    private final TeamRepository teamRepository;
    private final ParticipationRepository participationRepository;
    private Battle battle;

    @Autowired
    public JoinLeaveBattleControllerTest(JoinLeaveBattleController joinLeaveBattleController, BattleRepository battleRepository, TeamRepository teamRepository, ParticipationRepository participationRepository) {
        this.joinLeaveBattleController = joinLeaveBattleController;
        this.battleRepository = battleRepository;
        this.teamRepository = teamRepository;
        this.participationRepository = participationRepository;
    }

    @BeforeAll
    public void setUp() {
        battle = new Battle();
        battle.setRepositoryLink("link");
        battleRepository.save(battle);
    }

    @Test
    public void joinBattleLeaveBattle() {
        // Join
        Long idStudent = 1L;
        ResponseEntity<Object> response = joinLeaveBattleController.joinBattle(new StudentBattle(idStudent, battle.getBattleId()));
        assertTrue(response.getStatusCode().is2xxSuccessful());

        Optional<Team> optionalTeam = participationRepository.findTeamByBattleIdAndStudentId(idStudent, battle.getBattleId());

        assertTrue(optionalTeam.isPresent());
        assertTrue(teamRepository.existsById(optionalTeam.get().getTeamId()));

        // Leave
        response = joinLeaveBattleController.leaveBattle(new StudentBattle(idStudent, battle.getBattleId()));
        assertTrue(response.getStatusCode().is2xxSuccessful());

        Optional<Participation> participation = participationRepository.findById(new ParticipationId(idStudent, optionalTeam.get()));
        assertTrue(participation.isEmpty());
        optionalTeam = participationRepository.findTeamByBattleIdAndStudentId(idStudent, battle.getBattleId());
        assertTrue(optionalTeam.isEmpty());
    }

    @AfterAll
    void tearDown() {
        participationRepository.deleteAll();
        teamRepository.deleteAll();
        battleRepository.deleteAll();
    }
}