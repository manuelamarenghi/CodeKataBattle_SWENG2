package tournament;

import ckb.dto.account.Role;
import ckb.dto.account.SignUpRequest;
import ckb.dto.tournament.NewTournamentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TournamentServiceIntegrationTests {
    private static final String accountManagerUri = "http://localhost:8086";
    private static final WebClient webClient = WebClient.create();
    private final int GENERATE_EMAIL_LENGTH = 20;
    private final String tournamentManagerUri = "http://localhost:8087";
    private final WebTestClient webTestClient = WebTestClient.bindToServer().build();


    @Test
    public void createTournamentTest() {
        Long idEducator = createEducator();
        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(new NewTournamentRequest(idEducator, "Catta's Tournament", new Date(3000 - 1900, Calendar.FEBRUARY, 1)))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void createTournamentDateInvalid() {
        Long idEducator = createEducator();
        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(new NewTournamentRequest(idEducator, "Pino's Tournament", new Date(2022 - 1900, Calendar.FEBRUARY, 1)))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void createTournamentTestStudent() {
        Long idStudent = createStudent();
        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(new NewTournamentRequest(idStudent, "Pino's Tournament", new Date(3000 - 1900, Calendar.FEBRUARY, 1)))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void createTournamentNullPointerCreatorID() {
        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(new NewTournamentRequest(null, "Pino's Tournament", new Date(3000 - 1900, Calendar.FEBRUARY, 1)))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void createTournamentNullPointers() {
        webTestClient.post()
                .uri(tournamentManagerUri + "/api/tournament/new-tournament")
                .bodyValue(new NewTournamentRequest(null, null, null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
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

    private Long createStudent() {
        SignUpRequest request = SignUpRequest.builder()
                .email(getRandomString() + "@mail.com")
                .fullName("Luca Cattani")
                .password("password")
                .role(Role.STUDENT)
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
