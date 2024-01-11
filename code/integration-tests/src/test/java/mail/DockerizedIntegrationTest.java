package mail;

import ckb.dto.account.Role;
import ckb.dto.account.SignUpRequest;
import ckb.dto.mail.AllStudentsMailRequest;
import ckb.dto.mail.DirectMailRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class DockerizedIntegrationTest {

    private final WebClient webClient = WebClient.create();
    private static final String SCRIPTS_PATH = getScriptsPath();
    private static final int NUM_OF_CONTAINERS = 2;
    private final int GENERATE_EMAIL_LENGTH = 20;

    private final String accountManagerURI = "http://localhost:8086";
    private final String mailServiceURI = "http://localhost:8085";

    @BeforeAll
    public static void setUp() {
        // Start the containers
        ProcessBuilder processBuilder = new ProcessBuilder(SCRIPTS_PATH + "start-containers.sh").redirectErrorStream(true);
        Process process = runProcessBuilder(processBuilder);

        // Read the output of the process and check for services started
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            int startedContainers = 0;
            while ((line = reader.readLine()) != null && startedContainers < NUM_OF_CONTAINERS) {
                System.out.println(line);
                if (line.contains("on port")) startedContainers++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void tearDown() throws IOException {
        new ProcessBuilder(SCRIPTS_PATH + "stop-containers.sh").start();
        System.out.println("Containers stopped");
    }

    @Test
    public void directEmailSenderTest() {
        String email = getRandomString() + "@mail.com";
        String userID = createTestUser(email);

        DirectMailRequest request = DirectMailRequest.builder()
                .content("Hello World!")
                .userIDs(List.of(userID))
                .build();

        String response = webClient.post()
                .uri(mailServiceURI + "/api/mail/direct")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        assertEquals("OK", response);
    }

    @Test
    public void allStudentsEmailSenderTest() {
        AllStudentsMailRequest request = AllStudentsMailRequest.builder()
                .content("Hello World!")
                .build();

        String response = webClient.post()
                .uri(mailServiceURI + "/api/mail/all-students")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        assertEquals("OK", response);
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

    private static String getScriptsPath() {
        String path = DockerizedIntegrationTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return path.substring(0, path.indexOf("/target/test-classes")) + "/src/test/scripts/";
    }

    private static Process runProcessBuilder(ProcessBuilder processBuilder) {
        try {
            return processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getRandomString() {
        return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                .limit(GENERATE_EMAIL_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
