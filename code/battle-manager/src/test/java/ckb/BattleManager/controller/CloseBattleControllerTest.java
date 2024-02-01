package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.CloseBattleRequest;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.repository.BattleRepository;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CloseBattleControllerTest {
    private final ClientAndServer mockServerMailService = ClientAndServer.startClientAndServer(8085);
    private final ClientAndServer mockServerTournamentService = ClientAndServer.startClientAndServer(8087);
    @Autowired
    private CloseBattleController closeBattleController;
    @Autowired
    private BattleRepository battleRepository;

    @BeforeAll
    public void beforeAll() {
        closeBattleController.initTestMode();
        mockServerMailService
                .when(request().withMethod("POST").withPath("/api/mail/direct"))
                .respond(response().withStatusCode(200));

        mockServerTournamentService
                .when(request().withMethod("POST").withPath("/api/tournament/update-score"))
                .respond(response().withStatusCode(200));
    }

    @AfterEach
    public void afterEach() {
        battleRepository.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        mockServerMailService.stop();
        mockServerTournamentService.stop();
    }

    @Test
    public void closeBattleController() {
        Battle battle = Battle.builder()
                .name("Test close battle")
                .authorId(1L)
                .hasStarted(true)
                .hasEnded(true)
                .isClosed(false)
                .battleToEval(true)
                .regDeadline(LocalDateTime.now().minusMinutes(2))
                .subDeadline(LocalDateTime.now().minusMinutes(1))
                .build();
        battleRepository.save(battle);

        ResponseEntity<Object> response = closeBattleController.closeBattle(
                new CloseBattleRequest(
                        battle.getBattleId(),
                        1L
                )
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(battle.getHasEnded());
    }

    @Test
    public void closeBattleWrongEducator() {
        Battle battle = Battle.builder()
                .name("Test close battle")
                .authorId(1L)
                .hasStarted(true)
                .hasEnded(true)
                .hasEnded(false)
                .battleToEval(true)
                .regDeadline(LocalDateTime.now().minusMinutes(2))
                .subDeadline(LocalDateTime.now().minusMinutes(1))
                .build();
        battleRepository.save(battle);

        ResponseEntity<Object> response = closeBattleController.closeBattle(
                new CloseBattleRequest(
                        battle.getBattleId(),
                        2L
                )
        );

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void closeBattleAlreadyClosed() {
        Battle battle = Battle.builder()
                .name("Test close battle")
                .authorId(1L)
                .hasStarted(true)
                .hasEnded(true)
                .hasEnded(true)
                .battleToEval(true)
                .regDeadline(LocalDateTime.now().minusMinutes(2))
                .subDeadline(LocalDateTime.now().minusMinutes(1))
                .build();
        battleRepository.save(battle);

        ResponseEntity<Object> response = closeBattleController.closeBattle(
                new CloseBattleRequest(
                        battle.getBattleId(),
                        1L
                )
        );

        assertTrue(response.getStatusCode().is4xxClientError());
    }
}