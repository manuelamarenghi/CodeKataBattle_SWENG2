package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.AssignScoreRequest;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.ParticipationRepository;
import ckb.BattleManager.repository.TeamRepository;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
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
    private final AssignScoreController assignScoreController;
    private final ClientAndServer mockServerGithubManager = ClientAndServer.startClientAndServer(8083);
    private final ClientAndServer mockServerMailService = ClientAndServer.startClientAndServer(8085);
    private final ClientAndServer mockServerTournamentManager = ClientAndServer.startClientAndServer(8087);

    @Autowired
    public BattleScheduledServiceTest(BattleRepository battleRepository,
                                      TeamRepository teamRepository,
                                      ParticipationRepository participationRepository,
                                      SendTeamsPointsController sendTeamsPointsController,
                                      CreateGHRepositoryBattleController createGHRepositoryBattleController,
                                      AssignScoreController assignScoreController) {
        this.battleRepository = battleRepository;
        this.teamRepository = teamRepository;
        this.participationRepository = participationRepository;
        this.sendTeamsPointsController = sendTeamsPointsController;
        this.createGHRepositoryBattleController = createGHRepositoryBattleController;
        this.assignScoreController = assignScoreController;
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
        battleToStart.setIsClosed(false);
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
    void startBattlesWithAnIncorrectTeam() throws InterruptedException {
        Battle battleToStart = Battle.builder()
                .tournamentId(1L)
                .name("Test battle")
                .regDeadline(LocalDateTime.now().plusSeconds(3))
                .subDeadline(LocalDateTime.now().plusMinutes(3))
                .repositoryLink("link1")
                .hasStarted(false)
                .hasEnded(false)
                .isClosed(false)
                .minStudents(2)
                .maxStudents(3)
                .build();
        battleRepository.save(battleToStart);

        Team team1 = new Team();
        team1.setBattle(battleToStart);
        team1.setScore(10);
        team1.setRepositoryLink("linkTeam");
        team1.setCanParticipateToBattle(true);

        Participation participation1 = new Participation();
        participation1.setStudentId(2L);
        participation1.setTeam(team1);

        Participation participation2 = new Participation();
        participation2.setStudentId(3L);
        participation2.setTeam(team1);

        team1.setParticipation(
                List.of(participation1, participation2)
        );
        teamRepository.save(team1);

        Team team2 = new Team();
        team2.setBattle(battleToStart);
        team2.setScore(10);
        team2.setRepositoryLink("linkTeam");
        team2.setCanParticipateToBattle(false);
        teamRepository.save(team2);

        Optional<Battle> optionalRetrievedBattle = battleRepository.findById(battleToStart.getBattleId());
        assertTrue(optionalRetrievedBattle.isPresent());
        Battle retrievedBattle = optionalRetrievedBattle.get();
        assertFalse(retrievedBattle.getHasStarted());

        Thread.sleep(6000);

        optionalRetrievedBattle = battleRepository.findById(battleToStart.getBattleId());
        assertTrue(optionalRetrievedBattle.isPresent());
        retrievedBattle = optionalRetrievedBattle.get();
        assertTrue(retrievedBattle.getHasStarted());

        ResponseEntity<Object> response = assignScoreController.assignScore(
                new AssignScoreRequest(team1.getTeamId(), 100)
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());

        response = assignScoreController.assignScore(
                new AssignScoreRequest(team2.getTeamId(), 100)
        );
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void startBattlesWithAnIncorrectTeam2() throws InterruptedException {
        Battle battleToStart = Battle.builder()
                .tournamentId(1L)
                .name("Test battle")
                .regDeadline(LocalDateTime.now().plusSeconds(3))
                .subDeadline(LocalDateTime.now().plusMinutes(3))
                .repositoryLink("link1")
                .hasStarted(false)
                .hasEnded(false)
                .isClosed(false)
                .minStudents(2)
                .maxStudents(3)
                .build();
        battleRepository.save(battleToStart);

        Team team3 = new Team();
        team3.setBattle(battleToStart);
        team3.setScore(10);
        team3.setRepositoryLink("linkTeam");
        team3.setCanParticipateToBattle(true);
        teamRepository.save(team3);

        Participation participation3 = new Participation();
        participation3.setStudentId(2L);
        participation3.setTeam(team3);

        Thread.sleep(6000);


        ResponseEntity<Object> response = assignScoreController.assignScore(
                new AssignScoreRequest(team3.getTeamId(), 100)
        );
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void closeBattles() throws InterruptedException {
        sendTeamsPointsController.initTestMode();
        Battle battleToClose = new Battle();
        battleToClose.setTournamentId(1L);
        battleToClose.setName("Test battle");
        battleToClose.setRegDeadline(LocalDateTime.now());
        battleToClose.setSubDeadline(LocalDateTime.now().plusSeconds(3));
        battleToClose.setRepositoryLink("link2");
        battleToClose.setHasStarted(true);
        battleToClose.setHasEnded(false);
        battleToClose.setIsClosed(false);
        battleToClose.setBattleToEval(false);
        battleRepository.save(battleToClose);

        Team team = new Team();
        team.setBattle(battleToClose);
        team.setScore(10);
        team.setRepositoryLink("linkTeam");
        team.setCanParticipateToBattle(true);
        teamRepository.save(team);

        Participation participation = new Participation();
        participation.setStudentId(1L);
        participation.setTeam(team);

        participationRepository.save(
                participation
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
        assertTrue(retrievedBattle.getIsClosed());
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