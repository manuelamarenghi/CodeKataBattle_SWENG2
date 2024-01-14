package ckb.BattleManager;

import ckb.BattleManager.controller.RegisterStudentToTeamController;
import ckb.BattleManager.dto.input.StudentTeam;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.ParticipationRepository;
import ckb.BattleManager.repository.TeamRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegisterStudentToTeamControllerTest {
    private final RegisterStudentToTeamController registerStudentToTeamController;
    private final BattleRepository battleRepository;
    private final TeamRepository teamRepository;
    private final ParticipationRepository participationRepository;
    private Team team;

    @Autowired
    public RegisterStudentToTeamControllerTest(RegisterStudentToTeamController registerStudentToTeamController,
                                               TeamRepository teamRepository,
                                               ParticipationRepository participationRepository,
                                               BattleRepository battleRepository) {
        this.registerStudentToTeamController = registerStudentToTeamController;
        this.battleRepository = battleRepository;
        this.teamRepository = teamRepository;
        this.participationRepository = participationRepository;
    }

    @BeforeEach
    void setUp() {
        Battle battle = new Battle();
        battleRepository.save(battle);
        team = new Team();
        team.setBattle(battle);
        teamRepository.save(team);
    }

    @Test
    void registerStudentToTeam() {
        ResponseEntity<Object> response = registerStudentToTeamController.registerStudentToTeam(
                new StudentTeam(1L, team.getTeamId())
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(participationRepository.existsById(new ParticipationId(1L, team)));
    }

    @AfterEach
    void tearDown() {
        participationRepository.deleteAll();
        teamRepository.deleteAll();
        battleRepository.deleteAll();
    }
}