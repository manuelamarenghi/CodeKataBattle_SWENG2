package ckb.BattleManager;

import ckb.BattleManager.controller.GetBattleController;
import ckb.BattleManager.dto.input.GetBattleRequest;
import ckb.BattleManager.dto.output.BattleInfoMessage;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetBattleControllerTest {
    @Autowired
    private GetBattleController getBattleController;

    @Autowired
    private BattleRepository battleRepository;

    private Battle battle1, battle2, battle3;
    private Team team1, team2, team3;

    @BeforeAll
    public void setUp() {
        battle1 = Battle.builder()
                .tournamentId(1L)
                .repositoryLink("link1")
                .minStudents(1)
                .maxStudents(1)
                .regDeadline(LocalDateTime.now().plusDays(1))
                .subDeadline(LocalDateTime.now().plusDays(2))
                .battleToEval(true)
                .authorId(1L)
                .hasStarted(false)
                .hasEnded(false)
                .isClosed(false)
                .build();
        team1 = Team.builder().battle(battle1).score(0).build();
        team2 = Team.builder().battle(battle1).score(10).build();
        team3 = Team.builder().battle(battle1).score(20).build();
        battle1.setTeamsRegistered(List.of(team1, team2, team3));
        battleRepository.save(battle1);

        battle2 = Battle.builder()
                .tournamentId(1L)
                .repositoryLink("link2")
                .minStudents(1)
                .maxStudents(2)
                .regDeadline(LocalDateTime.now().plusDays(1))
                .subDeadline(LocalDateTime.now().plusDays(2))
                .battleToEval(true)
                .teamsRegistered(null)
                .authorId(1L)
                .hasStarted(false)
                .hasEnded(false)
                .isClosed(false)
                .build();
        battleRepository.save(battle2);

        battle3 = Battle.builder()
                .tournamentId(2L)
                .repositoryLink("link3")
                .minStudents(1)
                .maxStudents(2)
                .regDeadline(LocalDateTime.now().plusDays(1))
                .subDeadline(LocalDateTime.now().plusDays(2))
                .battleToEval(true)
                .teamsRegistered(null)
                .authorId(1L)
                .hasStarted(false)
                .hasEnded(false)
                .isClosed(false)
                .build();
        battleRepository.save(battle3);
    }

    @Test
    public void getBattle() {
        ResponseEntity<BattleInfoMessage> battleResponse = getBattleController.getBattle(new GetBattleRequest(battle1.getBattleId()));

        assertTrue(battleResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(battleResponse.getBody());
        BattleInfoMessage battleInfoMessage = battleResponse.getBody();
        List<Pair<Long, Integer>> pairsIdTeamPoints = battleInfoMessage.getPairsIdTeamPoints();
        assertEquals(3, pairsIdTeamPoints.size());
        assertEquals(team3.getTeamId(), pairsIdTeamPoints.get(0).getLeft());
        assertEquals(team2.getTeamId(), pairsIdTeamPoints.get(1).getLeft());
        assertEquals(team1.getTeamId(), pairsIdTeamPoints.get(2).getLeft());
    }

    @Test
    public void getBattlesOfTournament() {
        ResponseEntity<List<Long>> result = getBattleController.getBattlesOfTournament(new GetBattleRequest(1L));

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        System.out.println(result.getBody());
        assertEquals(2, result.getBody().size());
    }

    @AfterAll
    public void tearDown() {
        battleRepository.deleteAll();
    }
}
