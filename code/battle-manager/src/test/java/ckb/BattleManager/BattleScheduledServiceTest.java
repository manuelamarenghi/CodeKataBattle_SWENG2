package ckb.BattleManager;

import ckb.BattleManager.controller.SendTeamsPointsFinishedBattleController;
import ckb.BattleManager.controller.StartBattleController;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.ParticipationRepository;
import ckb.BattleManager.repository.TeamRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BattleScheduledServiceTest {
    private final BattleRepository battleRepository;
    private final TeamRepository teamRepository;
    private final ParticipationRepository participationRepository;
    private final SendTeamsPointsFinishedBattleController sendTeamsPointsFinishedBattleController;
    private final StartBattleController startBattleController;
    private ClientAndServer mockServer;

    @Autowired
    public BattleScheduledServiceTest(BattleRepository battleRepository,
                                      TeamRepository teamRepository,
                                      ParticipationRepository participationRepository,
                                      SendTeamsPointsFinishedBattleController sendTeamsPointsFinishedBattleController,
                                      StartBattleController startBattleController) {
        this.battleRepository = battleRepository;
        this.teamRepository = teamRepository;
        this.participationRepository = participationRepository;
        this.sendTeamsPointsFinishedBattleController = sendTeamsPointsFinishedBattleController;
        this.startBattleController = startBattleController;
    }

    @Test
    void startBattles() throws InterruptedException {
        startBattleController.initDebug();
        mockServer = ClientAndServer.startClientAndServer(8083);
        mockServer
                .when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/github/create-repo"))
                .respond(HttpResponse.response()
                        .withStatusCode(200));

        Battle battleToStart = new Battle();
        battleToStart.setTournamentId(1L);
        battleToStart.setRegDeadline(LocalDateTime.now().plusSeconds(3));
        battleToStart.setSubDeadline(LocalDateTime.now().plusMinutes(3));
        battleToStart.setRepositoryLink("link1");
        battleToStart.setHasStarted(false);
        battleToStart.setHasEnded(false);
        battleRepository.save(battleToStart);

        Optional<Battle> optionalRetrievedBattle = battleRepository.findById(battleToStart.getBattleId());
        assertTrue(optionalRetrievedBattle.isPresent());
        Battle retrievedBattle = optionalRetrievedBattle.get();
        assertFalse(retrievedBattle.getHasStarted());

        Thread.sleep(6000);

        optionalRetrievedBattle = battleRepository.findById(battleToStart.getBattleId());
        assertTrue(optionalRetrievedBattle.isPresent());
        retrievedBattle = optionalRetrievedBattle.get();
        assertTrue(retrievedBattle.getHasStarted());
    }

    @Test
    void closeBattles() throws InterruptedException {
        sendTeamsPointsFinishedBattleController.initDebug();
        Battle battleToClose = new Battle();
        battleToClose.setTournamentId(1L);
        battleToClose.setRegDeadline(LocalDateTime.now());
        battleToClose.setSubDeadline(LocalDateTime.now().plusSeconds(3));
        battleToClose.setRepositoryLink("link2");
        battleToClose.setHasStarted(true);
        battleToClose.setHasEnded(false);
        battleToClose.setBattleToEval(false);
        battleRepository.save(battleToClose);

        Team team = new Team();
        team.setBattle(battleToClose);
        team.setScore(10);
        team.setRepositoryLink("linkTeam");
        teamRepository.save(team);

        participationRepository.save(
                new Participation(
                        new ParticipationId(
                                1L, team
                        )
                )
        );

        // TODO: port!!!
        mockServer = ClientAndServer.startClientAndServer(8084);
        mockServer
                .when(HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/api/tournament/UpdateScore")
                )
                .respond(HttpResponse.response()
                        .withStatusCode(200));

        Optional<Battle> optionalRetrievedBattle = battleRepository.findById(battleToClose.getBattleId());
        assertTrue(optionalRetrievedBattle.isPresent());
        Battle retrievedBattle = optionalRetrievedBattle.get();
        assertFalse(retrievedBattle.getHasEnded());

        Thread.sleep(6000);

        optionalRetrievedBattle = battleRepository.findById(battleToClose.getBattleId());
        assertTrue(optionalRetrievedBattle.isPresent());
        retrievedBattle = optionalRetrievedBattle.get();
        assertTrue(retrievedBattle.getHasEnded());
    }

    @AfterEach
    void tearDownEach() {
        mockServer.stop();
    }

    @AfterAll
    void tearDown() {
        participationRepository.deleteAll();
        teamRepository.deleteAll();
        battleRepository.deleteAll();
    }
}