package ckb.BattleManager;

import ckb.BattleManager.controller.AssignScoreController;
import ckb.BattleManager.dto.input.PairTeamScore;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.TeamRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AssignScoreControllerTest {
    private final AssignScoreController assignScoreController;
    private final TeamRepository teamRepository;
    private final BattleRepository battleRepository;
    private Team team;

    @Autowired
    public AssignScoreControllerTest(AssignScoreController assignScoreController, TeamRepository teamRepository, BattleRepository battleRepository) {
        this.assignScoreController = assignScoreController;
        this.teamRepository = teamRepository;
        this.battleRepository = battleRepository;
    }

    @BeforeEach
    void setUp() {
        Battle battle = new Battle();
        battle.setTournamentId(1L);
        battle.setRepositoryLink("link");
        battle.setRegDeadline(LocalDateTime.now().minusMinutes(10));
        battle.setSubDeadline(LocalDateTime.now().plusMinutes(1));
        battle.setHasStarted(true);
        battle.setHasEnded(false);
        battle.setIsClosed(false);

        team = new Team();
        team.setBattle(battle);
        team.setRepositoryLink("team_link");
        team.setScore(0);
        team.setEduEvaluated(false);

        battleRepository.save(battle);
        teamRepository.save(team);
    }

    @Test
    public void assignScore() {
        ResponseEntity<Object> response = assignScoreController.assignScore(
                new PairTeamScore(team.getTeamId(), 100));

        Optional<Team> teamRetrieved = teamRepository.findById(team.getTeamId());
        if (teamRetrieved.isPresent()) {
            assertNotNull(response);
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(100, teamRetrieved.get().getScore());
        }
    }

    @Test
    public void assignPersonalScore() {
        ResponseEntity<Object> response = assignScoreController.assignPersonalScore(
                new PairTeamScore(team.getTeamId(), 50));

        Optional<Team> teamRetrieved = teamRepository.findById(team.getTeamId());
        if (teamRetrieved.isPresent()) {
            assertNotNull(response);
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(50, teamRetrieved.get().getScore());
            assertTrue(teamRetrieved.get().getEduEvaluated());
        }
    }

    @Test
    public void assignScoreAndPersonalScore() {
        int scoreSubmission = 60, scorePersonal = 110;
        ResponseEntity<Object> response1 = assignScoreController.assignScore(
                new PairTeamScore(team.getTeamId(), scoreSubmission));
        ResponseEntity<Object> response2 = assignScoreController.assignPersonalScore(
                new PairTeamScore(team.getTeamId(), scorePersonal));

        Optional<Team> teamRetrieved = teamRepository.findById(team.getTeamId());
        if (teamRetrieved.isPresent()) {
            assertNotNull(response1);
            assertNotNull(response2);
            assertTrue(response1.getStatusCode().is2xxSuccessful());
            assertTrue(response2.getStatusCode().is2xxSuccessful());

            assertEquals(scorePersonal + scoreSubmission, teamRetrieved.get().getScore());
            assertTrue(teamRetrieved.get().getEduEvaluated());
        }
    }

    @AfterEach
    void tearDown() {
        teamRepository.deleteAll();
        battleRepository.deleteAll();
    }
}