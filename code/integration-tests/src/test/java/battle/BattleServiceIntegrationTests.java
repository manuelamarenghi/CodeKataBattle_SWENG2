package battle;

import ckb.dto.account.Role;
import ckb.dto.account.SignUpRequest;
import ckb.dto.battle.*;
import ckb.dto.tournament.CloseTournamentRequest;
import ckb.dto.tournament.NewTournamentRequest;
import ckb.dto.tournament.SubscriptionRequest;
import ckb.model.Battle;
import ckb.model.Tournament;
import ckb.model.WorkingPair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BattleServiceIntegrationTests {
    private final String battleManagerUri = "http://localhost:8082";
    private static final String accountManagerUri = "http://localhost:8086";
    private final String tournamentManagerUri = "http://localhost:8087";
    private static final WebClient webClient = WebClient.create();
    private final int GENERATE_EMAIL_LENGTH = 20;
    private final WebTestClient webTestClient = WebTestClient.bindToServer()
            .responseTimeout(Duration.ofSeconds(30)).build();
    private static Long cattaId;

    @BeforeAll
    public static void init() {
        cattaId = createStudentCatta();
    }

    private static Long createStudentCatta() {
        SignUpRequest request = SignUpRequest.builder()
                .email("luca.cattani@mail.polimi.it")
                .fullName("Test Student")
                .password("password")
                .role(Role.STUDENT)
                .build();

        ResponseEntity<Long> userIDResponseEntity = webClient.post()
                .uri(accountManagerUri + "/api/account/sign-up")
                .bodyValue(request)
                .retrieve()
                .toEntity(Long.class)
                .block();

        assertNotNull(userIDResponseEntity);
        Long body = userIDResponseEntity.getBody();
        assertNotNull(body);
        assertTrue(body > 0);
        return body;
    }

    @Test
    public void createBattleTest() {
        Long educatorID = createEducator();

        ResponseEntity<Tournament> tournamentResponseEntity = webClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(
                        new NewTournamentRequest(
                                educatorID, "Pino's Tournament",
                                Date.from(
                                        LocalDateTime.now().plusMinutes(10)
                                                .atZone(ZoneId.systemDefault()).toInstant()
                                )
                        )
                )
                .retrieve()
                .toEntity(Tournament.class)
                .block();

        assertNotNull(tournamentResponseEntity);
        assertNotNull(tournamentResponseEntity.getBody());
        Tournament tournament = tournamentResponseEntity.getBody();

        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/subscription")
                .bodyValue(new SubscriptionRequest(tournament.getTournamentID(), cattaId))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient.post()
                .uri(battleManagerUri + "/api/battle/create-battle")
                .bodyValue(new CreateBattleRequest(
                        tournament.getTournamentID(),
                        "Test Battle " + getRandomString(),
                        educatorID,
                        1, 2, false,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2),
                        List.of(
                                new WorkingPair<>("tests/input_1.txt", "1"),
                                new WorkingPair<>("tests/output_1.txt", "2")
                        )
                ))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void closeTournamentTest() throws InterruptedException {
        Long educatorID = createEducator();

        ResponseEntity<Tournament> tournamentResponseEntity = webClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(
                        new NewTournamentRequest(
                                educatorID, "Pino's Closed Tournament",
                                Date.from(
                                        LocalDateTime.now().plusSeconds(10)
                                                .atZone(ZoneId.of(
                                                        "Europe/Rome"
                                                )).toInstant()
                                )
                        )
                )
                .retrieve()
                .toEntity(Tournament.class)
                .block();

        assertNotNull(tournamentResponseEntity);
        assertNotNull(tournamentResponseEntity.getBody());
        Tournament tournament = tournamentResponseEntity.getBody();

        webTestClient.post()
                .uri(battleManagerUri + "/api/battle/create-battle")
                .bodyValue(new CreateBattleRequest(
                        tournament.getTournamentID(),
                        "Test Battle" + getRandomString(),
                        educatorID,
                        1, 2, false,
                        LocalDateTime.now().plusSeconds(5).atZone(ZoneId.of("Europe/Rome")).toLocalDateTime(),
                        LocalDateTime.now().plusSeconds(20).atZone(ZoneId.of("Europe/Rome")).toLocalDateTime(),
                        List.of(
                                new WorkingPair<>("tests/input_1.txt", "1"),
                                new WorkingPair<>("tests/output_1.txt", "2")
                        )
                ))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful();

        // Sleep 15 seconds
        Thread.sleep(21000);

        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/close-tournament")
                .bodyValue(
                        new CloseTournamentRequest(tournament.getTournamentID(), educatorID)
                )
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void assignEvaluationToTeamAndCheckTheCorrectnessOfRanking() throws InterruptedException {
        Long educatorID = createEducator();

        ResponseEntity<Tournament> tournamentResponseEntity = webClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(
                        new NewTournamentRequest(
                                educatorID, "Pino's Evaluation Tournament",
                                Date.from(
                                        LocalDateTime.now().plusSeconds(10)
                                                .atZone(ZoneId.systemDefault()).toInstant()
                                )
                        )
                )
                .retrieve()
                .toEntity(Tournament.class)
                .block();

        assertNotNull(tournamentResponseEntity);
        assertNotNull(tournamentResponseEntity.getBody());
        Tournament tournament = tournamentResponseEntity.getBody();

        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/subscription")
                .bodyValue(new SubscriptionRequest(tournament.getTournamentID(), cattaId))
                .exchange()
                .expectStatus().is2xxSuccessful();

        ResponseEntity<Battle> battleResponseEntity = webClient.post()
                .uri(battleManagerUri + "/api/battle/create-battle")
                .bodyValue(new CreateBattleRequest(
                        tournament.getTournamentID(),
                        "Test Battle " + getRandomString(),
                        educatorID,
                        1, 2, false,
                        LocalDateTime.now().plusSeconds(25).atZone(ZoneId.of("Europe/Rome")).toLocalDateTime(),
                        LocalDateTime.now().plusSeconds(50).atZone(ZoneId.of("Europe/Rome")).toLocalDateTime(),
                        List.of(
                                new WorkingPair<>("tests/input_1.txt", "1"),
                                new WorkingPair<>("tests/output_1.txt", "2")
                        )
                ))
                .retrieve()
                .toEntity(Battle.class)
                .block();

        assertNotNull(battleResponseEntity);
        assertNotNull(battleResponseEntity.getBody());
        Battle battle = battleResponseEntity.getBody();

        webTestClient.post()
                .uri(battleManagerUri + "/api/battle/join-battle")
                .bodyValue(new JoinRequest(cattaId, battle.getBattleId()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        ResponseEntity<TeamsRankingMessage> teamsRankingResponseEntity = webClient
                .post()
                .uri(battleManagerUri + "/api/battle/get-teams-battle")
                .bodyValue(new GetTeamsRequest(battle.getBattleId()))
                .retrieve()
                .toEntity(TeamsRankingMessage.class)
                .block();

        Thread.sleep(26000);

        assertNotNull(teamsRankingResponseEntity);
        assertNotNull(teamsRankingResponseEntity.getBody());
        TeamsRankingMessage teamsRanking = teamsRankingResponseEntity.getBody();
        webTestClient.post()
                .uri(battleManagerUri + "/api/battle/assign-score")
                .bodyValue(
                        new AssignScoreRequest(
                                teamsRanking.getListTeamsIdScore().get(0).getLeft(),
                                20
                        )
                )
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        Thread.sleep(30000);

        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/close-tournament")
                .bodyValue(
                        new CloseTournamentRequest(tournament.getTournamentID(), educatorID)
                )
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void assignPersonalScoreTest() throws InterruptedException {
        Long educatorID = createEducator();

        ResponseEntity<Tournament> tournamentResponseEntity = webClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(
                        new NewTournamentRequest(
                                educatorID, "Pino's Personal Evaluation Tournament",
                                Date.from(
                                        LocalDateTime.now().plusSeconds(10)
                                                .atZone(ZoneId.systemDefault()).toInstant()
                                )
                        )
                )
                .retrieve()
                .toEntity(Tournament.class)
                .block();

        assertNotNull(tournamentResponseEntity);
        assertNotNull(tournamentResponseEntity.getBody());
        Tournament tournament = tournamentResponseEntity.getBody();

        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/subscription")
                .bodyValue(new SubscriptionRequest(tournament.getTournamentID(), cattaId))
                .exchange()
                .expectStatus().is2xxSuccessful();

        ResponseEntity<Battle> battleResponseEntity = webClient.post()
                .uri(battleManagerUri + "/api/battle/create-battle")
                .bodyValue(new CreateBattleRequest(
                        tournament.getTournamentID(),
                        "Test Battle " + getRandomString(),
                        educatorID,
                        1, 2, true,
                        LocalDateTime.now().plusSeconds(10),
                        LocalDateTime.now().plusSeconds(15),
                        List.of(
                                new WorkingPair<>("tests/input_1.txt", "1"),
                                new WorkingPair<>("tests/output_1.txt", "2")
                        )
                ))
                .retrieve()
                .toEntity(Battle.class)
                .block();

        assertNotNull(battleResponseEntity);
        assertNotNull(battleResponseEntity.getBody());
        Battle battle = battleResponseEntity.getBody();

        webTestClient.post()
                .uri(battleManagerUri + "/api/battle/join-battle")
                .bodyValue(new JoinRequest(cattaId, battle.getBattleId()))
                .exchange()
                .expectStatus().is2xxSuccessful();

        ResponseEntity<TeamsRankingMessage> teamsRankingResponseEntity = webClient
                .post()
                .uri(battleManagerUri + "/api/battle/get-teams-battle")
                .bodyValue(new GetTeamsRequest(battle.getBattleId()))
                .retrieve()
                .toEntity(TeamsRankingMessage.class)
                .block();

        assertNotNull(teamsRankingResponseEntity);
        assertNotNull(teamsRankingResponseEntity.getBody());
        TeamsRankingMessage teamsRanking = teamsRankingResponseEntity.getBody();
        webTestClient.post()
                .uri(battleManagerUri + "/api/battle/assign-personal-score")
                .bodyValue(
                        new AssignPersonalScoreRequest(
                                teamsRanking.getListTeamsIdScore().get(0).getLeft(),
                                5,
                                educatorID
                        )
                )
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        // Sleep 15 seconds
        Thread.sleep(30000);

        webTestClient.post()
                .uri(battleManagerUri + "/api/battle/close-battle")
                .bodyValue(
                        new CloseBattleRequest(
                                battle.getBattleId(),
                                educatorID
                        )
                )
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/close-tournament")
                .bodyValue(
                        new CloseTournamentRequest(tournament.getTournamentID(), educatorID)
                )
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    private Long createEducator() {
        SignUpRequest request = SignUpRequest.builder()
                .email(getRandomString() + "@mail.com")
                .fullName("Test Educator")
                .password("password")
                .role(Role.EDUCATOR)
                .build();

        Long userID = webClient.post()
                .uri(accountManagerUri + "/api/account/sign-up")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Long.class)
                .block();

        assertNotNull(userID);
        assertTrue(userID > 0);
        return userID;
    }

    private String getRandomString() {
        return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                .limit(GENERATE_EMAIL_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
