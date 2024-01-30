package battle;

import ckb.dto.account.Role;
import ckb.dto.account.SignUpRequest;
import ckb.dto.battle.CreateBattleRequest;
import ckb.dto.tournament.NewTournamentRequest;
import ckb.dto.tournament.SubscriptionRequest;
import ckb.model.Tournament;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
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

    @Test
    public void createBattleTest() {
        Long educatorID = createEducator();
        Long cattaId = createStudentCatta();

        ResponseEntity<Tournament> tournamentResponseEntity = webClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(new NewTournamentRequest(new Date(2024 - 1900, Calendar.FEBRUARY, 1), educatorID))
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
                        LocalDateTime.now().plusHours(2)
                ))
                .accept(MediaType.APPLICATION_JSON)
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

    private Long createStudentCatta() {
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
        if (userIDResponseEntity.getStatusCode().is2xxSuccessful()) {
            Long body = userIDResponseEntity.getBody();
            assertNotNull(body);
            assertTrue(body > 0);
            return body;
        }
        return null;
    }

    private String getRandomString() {
        return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                .limit(GENERATE_EMAIL_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
