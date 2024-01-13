package ckb.BattleManager;

import ckb.BattleManager.controller.JoinLeaveBattleController;
import ckb.BattleManager.dto.input.StudentBattle;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.ParticipationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JoinLeaveBattleControllerTest {
    private final JoinLeaveBattleController joinLeaveBattleController;
    private final BattleRepository battleRepository;
    private final ParticipationRepository participationRepository;
    private Battle battle;

    @Autowired
    public JoinLeaveBattleControllerTest(JoinLeaveBattleController joinLeaveBattleController, BattleRepository battleRepository, ParticipationRepository participationRepository) {
        this.joinLeaveBattleController = joinLeaveBattleController;
        this.battleRepository = battleRepository;
        this.participationRepository = participationRepository;
    }

    @BeforeEach
    void setUp() {
        battle = new Battle();
        battle.setRepositoryLink("link");
        battleRepository.save(battle);
    }

    @Test
    public void joinBattle() {
        Long idStudent = 1L;
        ResponseEntity<Object> response = joinLeaveBattleController.joinBattle(new StudentBattle(idStudent, battle.getBattleId()));
        assertTrue(response.getStatusCode().is2xxSuccessful());

        Optional<Team> optionalTeam = participationRepository.findTeamByBattleIdAndStudentId(idStudent, battle.getBattleId());
        assertTrue(optionalTeam.isPresent());
    }

    @Test
    public void leaveBattle() {
        Long idStudent = 1L;
        ResponseEntity<Object> response = joinLeaveBattleController.leaveBattle(new StudentBattle(idStudent, battle.getBattleId()));
        assertTrue(response.getStatusCode().is2xxSuccessful());

        Optional<Team> optionalTeam = participationRepository.findTeamByBattleIdAndStudentId(idStudent, battle.getBattleId());
        assertTrue(optionalTeam.isEmpty());
    }

    @AfterEach
    void tearDown() {
        battleRepository.deleteAll();
        participationRepository.deleteAll();
    }
}