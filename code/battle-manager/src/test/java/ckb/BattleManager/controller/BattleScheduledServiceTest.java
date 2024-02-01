package ckb.BattleManager.controller;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.ParticipationRepository;
import ckb.BattleManager.repository.TeamRepository;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BattleScheduledServiceTest {
    private final BattleRepository battleRepository;
    private final TeamRepository teamRepository;
    private final ParticipationRepository participationRepository;
    private final SendTeamsPointsController sendTeamsPointsController;
    private final CreateGHRepositoryBattleController createGHRepositoryBattleController;
    private final ClientAndServer mockServerGithubManager = ClientAndServer.startClientAndServer(8083);
    private final ClientAndServer mockServerMailService = ClientAndServer.startClientAndServer(8085);
    private final ClientAndServer mockServerTournamentManager = ClientAndServer.startClientAndServer(8087);

    @Autowired
    public BattleScheduledServiceTest(BattleRepository battleRepository,
                                      TeamRepository teamRepository,
                                      ParticipationRepository participationRepository,
                                      SendTeamsPointsController sendTeamsPointsController,
                                      CreateGHRepositoryBattleController createGHRepositoryBattleController) {
        this.battleRepository = battleRepository;
        this.teamRepository = teamRepository;
        this.participationRepository = participationRepository;
        this.sendTeamsPointsController = sendTeamsPointsController;
        this.createGHRepositoryBattleController = createGHRepositoryBattleController;
    }

    @BeforeAll
    public void beforeAll() {
        createGHRepositoryBattleController.initTestMode();
        sendTeamsPointsController.initTestMode();

        mockServerGithubManager
                .when(request().withMethod("POST").withPath("/api/github/make-public"))
                .respond(response().withStatusCode(200));

        mockServerMailService
                .when(request().withMethod("POST").withPath("/api/mail/direct"))
                .respond(response().withStatusCode(200));

        mockServerTournamentManager
                .when(request().withMethod("POST").withPath("/api/tournament/update-score"))
                .respond(response().withStatusCode(200));
    }

    @Test
    void startBattles() throws InterruptedException {
        Battle battleToStart = new Battle();
        battleToStart.setTournamentId(1L);
        battleToStart.setName("Test battle");
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
        sendTeamsPointsController.initTestMode();
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
    public void afterEach() {
        battleRepository.deleteAll();
        teamRepository.deleteAll();
        participationRepository.deleteAll();
    }

    @AfterAll
    void tearDown() {
        mockServerGithubManager.stop();
        mockServerMailService.stop();
        mockServerTournamentManager.stop();
    }
}