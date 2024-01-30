package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.dto.in.EvaluationRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EvaluationTest {

    @Autowired
    private CEvaluationController evaluateController;
    private ClientAndServer battleMockServer;

    @BeforeAll
    public void setUp() {
        evaluateController.initTestMode();
        battleMockServer = ClientAndServer.startClientAndServer(8082);

        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/assign-score"))
                .respond(response().withStatusCode(200));

        battleMockServer.when(request().withMethod("POST").withPath("/api/battle/official-repo-url"))
                .respond(response().withStatusCode(200).withBody("https://github.com/SigCatta/WordChecker.git"));
    }

    @AfterAll
    public void stopProxy() {
        battleMockServer.stop();
    }

    @Test
    public void test() {
        EvaluationRequest request = EvaluationRequest.builder()
                .teamId(1L)
                .repoUrl("https://github.com/SigCatta/WordChecker.git")
                .build();
        ResponseEntity<Object> response = evaluateController.evaluate(request);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

}
