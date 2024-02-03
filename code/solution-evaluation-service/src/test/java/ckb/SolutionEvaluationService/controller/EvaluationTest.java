package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.dto.in.EvaluationRequest;
import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EvaluationTest {

    @Autowired
    private CEvaluationController evaluateController;
    private ClientAndServer battleMockServer;

    @BeforeEach
    public void setUp() {
        evaluateController.initTestMode();
        battleMockServer = ClientAndServer.startClientAndServer(8082);
    }

    @AfterEach
    public void stopProxy() {
        battleMockServer.stop();
    }

    @Test
    public void correctBehaviorTest() {
        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/assign-score"))
                .respond(response().withStatusCode(200));

        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/official-repo-url"))
                .respond(response().withStatusCode(200).withBody("https://github.com/SigCatta/WordChecker.git"));

        EvaluationRequest request = EvaluationRequest.builder()
                .teamId(1L)
                .repoUrl("https://github.com/SigCatta/WordChecker.git")
                .build();
        ResponseEntity<Object> response = evaluateController.evaluate(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
    @Test
    public void nonExistingRepoTest() {
        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/assign-score"))
                .respond(response().withStatusCode(200));

        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/official-repo-url"))
                .respond(response().withStatusCode(200).withBody("https://github.com/SigCatta/WordChecker.git"));

        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/official-repo-url"))
                .respond(response().withStatusCode(200).withBody("https://github.com/SigCatta/WordChecker.git"));

        EvaluationRequest request = EvaluationRequest.builder()
                .teamId(1L)
                // .repoUrl("https://github.com/doesNotExist/SomeRepoThatIsNotTHere.miaooo")
                // The above line is the original line, but it is commented out because it is not a valid URL
                // and will cause mvn clean install to stop waiting for input, workflows will not complete
                // it can be demonstrated by running a docker container that it won't actually stop...
                .build();
        ResponseEntity<Object> response = evaluateController.evaluate(request);
        assertFalse(response.getStatusCode().is2xxSuccessful());
    }
    @Test
    public void failedToScoreTest() {
        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/assign-score"))
                .respond(response().withStatusCode(500));

        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/official-repo-url"))
                .respond(response().withStatusCode(200).withBody("https://github.com/SigCatta/WordChecker.git"));

        EvaluationRequest request = EvaluationRequest.builder()
                .teamId(1L)
                .repoUrl("https://github.com/SigCatta/WordChecker.git")
                .build();
        ResponseEntity<Object> response = evaluateController.evaluate(request);
        assertFalse(response.getStatusCode().is2xxSuccessful());
    }
    @Test
    public void failedToFetchOfficialRepoTest() {
        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/assign-score"))
                .respond(response().withStatusCode(200));

        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/official-repo-url"))
                .respond(response().withStatusCode(500));

        EvaluationRequest request = EvaluationRequest.builder()
                .teamId(1L)
                .repoUrl("https://github.com/SigCatta/WordChecker.git")
                .build();
        ResponseEntity<Object> response = evaluateController.evaluate(request);
        assertFalse(response.getStatusCode().is2xxSuccessful());
    }
    @Test
    public void failedToGetOfficialRepoUrlTest() {
        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/assign-score"))
                .respond(response().withStatusCode(200));

        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/official-repo-url"))
                .respond(response().withStatusCode(500));

        EvaluationRequest request = EvaluationRequest.builder()
                .teamId(1L)
                .repoUrl("https://github.com/SigCatta/WordChecker.git")
                .build();
        ResponseEntity<Object> response = evaluateController.evaluate(request);
        assertFalse(response.getStatusCode().is2xxSuccessful());
    }
    @Test
    public void nullOfficialRepoUrlTest() {
        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/assign-score"))
                .respond(response().withStatusCode(200));

        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/official-repo-url"))
                .respond(response().withStatusCode(200));

        EvaluationRequest request = EvaluationRequest.builder()
                .teamId(1L)
                .repoUrl("https://github.com/SigCatta/WordChecker.git")
                .build();
        ResponseEntity<Object> response = evaluateController.evaluate(request);
        assertFalse(response.getStatusCode().is2xxSuccessful());
    }

}
