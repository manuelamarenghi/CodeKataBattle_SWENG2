package mail;

import ckb.ContainerHandler;
import ckb.dto.account.Role;
import ckb.dto.account.SignUpRequest;
import ckb.dto.mail.AllStudentsMailRequest;
import ckb.dto.mail.DirectMailRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MailServiceIntegrationTests {

    private final WebClient webClient = WebClient.create();
    private final int GENERATE_EMAIL_LENGTH = 20;
    private final String accountManagerURI = "http://localhost:8086";
    private final String mailServiceURI = "http://localhost:8085";
    private final WebTestClient webTestClient = WebTestClient.bindToServer().build();


    @BeforeAll
    public static void init() throws IOException, InterruptedException {
        ContainerHandler.stop();
        Thread.sleep(10000);
        ContainerHandler.start();
    }

    @AfterAll
    public static void close() throws IOException {
        ContainerHandler.stop();
    }
    @Test
    public void directTestWithCorrectBehaviour() {
        String email = getRandomString() + "@mail.com";
        String userID = createTestUser(email);

        DirectMailRequest request = DirectMailRequest.builder()
                .content("Hello World!")
                .userIDs(List.of(userID))
                .build();
        webTestClient.post()
                .uri(mailServiceURI + "/api/mail/direct")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void directTestPartiallyCorrect() {
        String email = getRandomString() + "@mail.com";
        String userID = createTestUser(email);

        DirectMailRequest request = DirectMailRequest.builder()
                .content("Hello World!")
                .userIDs(List.of(userID, "0"))
                .build();
        webTestClient.post()
                .uri(mailServiceURI + "/api/mail/direct")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void directTestWithInvalidUser() {
        DirectMailRequest request = DirectMailRequest.builder()
                .content("Hello World!")
                .userIDs(List.of("-1"))
                .build();
        webTestClient.post()
                .uri(mailServiceURI + "/api/mail/direct")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void allStudentsTestWithCorrectBehaviour() {
        AllStudentsMailRequest request = AllStudentsMailRequest.builder()
                .content("Hello World!")
                .build();
        webTestClient.post()
                .uri(mailServiceURI + "/api/mail/all-students")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void allStudentsTestWithEmptyObject() {
        AllStudentsMailRequest request = AllStudentsMailRequest.builder().build();
        webTestClient.post()
                .uri(mailServiceURI + "/api/mail/all-students")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void directTestWithEmptyObject() {
        DirectMailRequest request = DirectMailRequest.builder().build();
        webTestClient.post()
                .uri(mailServiceURI + "/api/mail/direct")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void directTestWithWrongObject() {
        AllStudentsMailRequest request = AllStudentsMailRequest.builder()
                .content("Hello World!")
                .build();
        webTestClient.post()
                .uri(mailServiceURI + "/api/mail/direct")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void allStudentsTestWithWrongObject() {
        DirectMailRequest request = DirectMailRequest.builder()
                .content("Hello World!")
                .userIDs(List.of("1", "2", "3"))
                .build();
        webTestClient.post()
                .uri(mailServiceURI + "/api/mail/all-students")
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    private String createTestUser(String email) {
        SignUpRequest request = SignUpRequest.builder()
                .email(email)
                .fullName("Test User")
                .password("password")
                .role(Role.STUDENT)
                .build();

        Long userID = webClient.post()
                .uri(accountManagerURI + "/api/account/sign-up")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Long.class)
                .block();

        assertNotNull(userID);
        assertTrue(userID > 0);
        return String.valueOf(userID);
    }

    private String getRandomString() {
        return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                .limit(GENERATE_EMAIL_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
