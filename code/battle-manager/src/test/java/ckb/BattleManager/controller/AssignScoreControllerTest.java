package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.AssignPersonalScoreRequest;
import ckb.BattleManager.dto.input.AssignScoreRequest;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AssignScoreControllerTest {
    private final AssignScoreController assignScoreController;
    private final TeamRepository teamRepository;
    private final BattleRepository battleRepository;
    private Team team1, team2;

    @Autowired
    public AssignScoreControllerTest(AssignScoreController assignScoreController, TeamRepository teamRepository, BattleRepository battleRepository) {
        this.assignScoreController = assignScoreController;
        this.teamRepository = teamRepository;
        this.battleRepository = battleRepository;
    }

    @BeforeEach
    void setUp() {
        Battle battle = Battle.builder()
                .tournamentId(1L)
                .repositoryLink("link1")
                .authorId(1L)
                .regDeadline(LocalDateTime.now().minusMinutes(10))
                .subDeadline(LocalDateTime.now().plusMinutes(20))
                .battleToEval(true)
                .hasStarted(true)
                .hasEnded(false)
                .isClosed(false)
                .build();

        team1 = new Team();
        team1.setBattle(battle);
        team1.setScore(0);
        team1.setEduEvaluated(false);
        team1.setCanParticipateToBattle(true);

        battle.setTeamsRegistered(List.of(team1));

        battleRepository.save(battle);

        Battle battle2 = Battle.builder()
                .tournamentId(1L)
                .repositoryLink("link2")
                .authorId(1L)
                .regDeadline(LocalDateTime.now().minusMinutes(10))
                .subDeadline(LocalDateTime.now().plusMinutes(20))
                .battleToEval(true)
                .hasStarted(true)
                .hasEnded(true)
                .isClosed(false)
                .build();

        team2 = new Team();
        team2.setBattle(battle2);
        team2.setScore(0);
        team2.setEduEvaluated(false);
        team2.setCanParticipateToBattle(true);

        battle2.setTeamsRegistered(List.of(team2));

        battleRepository.save(battle2);
    }

    @Test
    public void assignScore() {
        ResponseEntity<Object> response = assignScoreController.assignScore(
                new AssignScoreRequest(team1.getTeamId(), 100));

        Optional<Team> teamRetrieved = teamRepository.findById(team1.getTeamId());
        if (teamRetrieved.isPresent()) {
            assertNotNull(response);
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertEquals(100, teamRetrieved.get().getScore());
        }
    }

    @Test
    public void assignPersonalScore() {
        ResponseEntity<Object> response = assignScoreController.assignPersonalScore(
                new AssignPersonalScoreRequest(team2.getTeamId(), 10, 1L));

        Optional<Team> teamRetrieved = teamRepository.findById(team2.getTeamId());
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(teamRetrieved);
        assertTrue(teamRetrieved.isPresent());
        assertEquals(10, teamRetrieved.get().getScore());
        assertTrue(teamRetrieved.get().getEduEvaluated());
    }

    @Test
    public void assignScoreAndPersonalScore() {
        int scoreSubmission = 60, scorePersonal = 5;
        ResponseEntity<Object> response1 = assignScoreController.assignScore(
                new AssignScoreRequest(team2.getTeamId(), scoreSubmission));
        ResponseEntity<Object> response2 = assignScoreController.assignPersonalScore(
                new AssignPersonalScoreRequest(team2.getTeamId(), scorePersonal, 1L));
        ResponseEntity<Object> response3 = assignScoreController.assignPersonalScore(
                new AssignPersonalScoreRequest(team2.getTeamId(), scorePersonal, 1L));

        Optional<Team> teamRetrieved = teamRepository.findById(team2.getTeamId());

        assertNotNull(response1);
        assertNotNull(response2);
        assertTrue(response1.getStatusCode().is2xxSuccessful());
        assertTrue(response2.getStatusCode().is2xxSuccessful());

        assertTrue(teamRetrieved.isPresent());
        assertEquals(scorePersonal + scoreSubmission, teamRetrieved.get().getScore());
        assertTrue(teamRetrieved.get().getEduEvaluated());

        assertTrue(response3.getStatusCode().is4xxClientError());
    }

    @Test
    public void assignScoreNegative() {
        ResponseEntity<Object> response = assignScoreController.assignScore(
                new AssignScoreRequest(team1.getTeamId(), -100));

        assertNotNull(response);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void assignPersonalScoreNegative() {
        ResponseEntity<Object> response = assignScoreController.assignPersonalScore(
                new AssignPersonalScoreRequest(team1.getTeamId(), -100, 1L));

        assertNotNull(response);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void assignScoreWrongEducatorId() {
        ResponseEntity<Object> response = assignScoreController.assignPersonalScore(
                new AssignPersonalScoreRequest(team1.getTeamId(), 100, 2L));

        assertNotNull(response);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @AfterEach
    void tearDown() {
        teamRepository.deleteAll();
        battleRepository.deleteAll();
    }
}