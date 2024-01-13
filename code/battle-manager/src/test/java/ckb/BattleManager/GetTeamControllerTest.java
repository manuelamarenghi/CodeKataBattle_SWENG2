package ckb.BattleManager;

import ckb.BattleManager.controller.GetTeamController;
import ckb.BattleManager.dto.input.IdLong;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.TeamRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetTeamControllerTest {
    private final GetTeamController getTeamController;
    private final TeamRepository teamRepository;
    private final BattleRepository battleRepository;
    private Battle battle;

    @Autowired
    public GetTeamControllerTest(GetTeamController getTeamController, TeamRepository teamRepository, BattleRepository battleRepository) {
        this.getTeamController = getTeamController;
        this.teamRepository = teamRepository;
        this.battleRepository = battleRepository;
    }

    @BeforeAll
    void setUp() {
        battle = new Battle();
        battle.setTournamentId(1L);
        battle.setRepositoryLink("link");

        Team team = new Team();
        team.setBattle(battle);
        team.setRepositoryLink("team_link");
        team.setScore(0);
        team.setEduEvaluated(false);

        battleRepository.save(battle);
        teamRepository.save(team);
    }

    @Test
    public void getTeam() {
        long idTeam = 1L;
        ResponseEntity<Team> retrievedTeam = getTeamController.getTeam(new IdLong(idTeam));
        if (teamRepository.existsById(idTeam)) {
            assertTrue(retrievedTeam.getStatusCode().is2xxSuccessful());
            assertNotNull(retrievedTeam.getBody());
        } else {
            assertTrue(retrievedTeam.getStatusCode().is4xxClientError());
        }
    }

    @Test
    public void getTeamsOfBattle() {
        ResponseEntity<List<Team>> retrievedTeams = getTeamController.getTeamsOfBattle(new IdLong(battle.getBattleId()));
        if (teamRepository.existsByBattle(battle)) {
            assertTrue(retrievedTeams.getStatusCode().is2xxSuccessful());
            assertNotNull(retrievedTeams.getBody());
        } else {
            assertTrue(retrievedTeams.getStatusCode().is4xxClientError());
        }
    }

    @AfterAll
    void tearDown() {
        teamRepository.deleteAll();
        battleRepository.deleteAll();
    }
}