package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.dto.in.EvaluationRequest;
import ckb.SolutionEvaluationService.dto.out.AssignScoreRequest;
import ckb.SolutionEvaluationService.dto.out.OfficialRepoRequest;
import ckb.SolutionEvaluationService.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/solution-evaluation/c")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CEvaluationController extends Controller {
    private final EvaluationService evaluationService;
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> evaluate(@RequestBody EvaluationRequest request) {
        String repoUrl = request.getRepoUrl();

        // pull repo
        String path = evaluationService.pullRepo(repoUrl);
        if (!path.equals("ERR")) {
            log.info("Repository at " + repoUrl + " pulled successfully ");
        } else {
            log.error("Error pulling repository at " + repoUrl);
            return new ResponseEntity<>("Error pulling repo", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        // compile
        if (!evaluationService.compile("c", path)) {
            log.error("Compilation failed");
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Compilation failed", getHeaders(), HttpStatus.BAD_REQUEST);
        } else {
            log.info("Compilation successful");
        }

        // run tests
        int testsDeduction;
        try {
            String officialRepoUrl = getOfficialRepoUrl(request);
            testsDeduction = runTests("c", path, officialRepoUrl);
        } catch (Exception e) {
            log.error("Error executing tests");
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Error executing tests", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (testsDeduction >= 100) {
            log.info("Tests failed, deduction >= 100 points, exiting...");
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Tests failed, 0 points", getHeaders(), HttpStatus.OK);
        }


        // run static analysis
        int staticAnalysisDeduction = evaluationService.executeStaticAnalysis("c", path);
        if (staticAnalysisDeduction < 0) {
            log.error("Error executing static analysis");
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Error executing static analysis", getHeaders(), HttpStatus.BAD_REQUEST);
        } else {
            log.info("Deduction: " + staticAnalysisDeduction);
        }

        // calculate score
        int score = 100 - testsDeduction - staticAnalysisDeduction;
        log.info("Evaluation completed for " + repoUrl + " score: " + score);
        if (score < 0) {
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Evaluation successful, please get better...", getHeaders(), HttpStatus.OK);
        }

        // update score
        ResponseEntity<Object> response;
        try{
            AssignScoreRequest scoreUpdateRequest = AssignScoreRequest.builder()
                    .idTeam(request.getTeamId())
                    .score(score)
                    .build();
            response = webClient.post()
                    .uri(battleManagerUrl + "/api/battle/assign-score")
                    .bodyValue(scoreUpdateRequest)
                    .retrieve()
                    .toEntity(Object.class)
                    .block();
        } catch (Exception e) {
            log.error("Error while updating scores");
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Error updating scores", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            log.error("Error while updating scores");
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Error updating scores", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else log.info("Score updated successfully");

        try {
            evaluationService.cleanUp(path);
            log.info("Clean up successful, deleted " + path + " directory");
        } catch (Exception e) {
            log.error("Error cleaning up " + path);
            return new ResponseEntity<>("Error cleaning up", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Evaluation successful, you get some points :)", getHeaders(), HttpStatus.OK);
    }

    private String getOfficialRepoUrl(EvaluationRequest request) {
        OfficialRepoRequest officialRepoRequest = OfficialRepoRequest.builder()
                .teamId(request.getTeamId())
                .build();
        ResponseEntity<String> response = webClient.post()
                .uri(battleManagerUrl + "/api/battle/official-repo-url")
                .bodyValue(officialRepoRequest)
                .retrieve()
                .toEntity(String.class)
                .block();
        if (response == null || !response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Error getting official repo url");
            throw new RuntimeException("Error getting official repo url");
        } else {
            log.info("Official repo url: " + response.getBody());
            return response.getBody();
        }
    }

    private int runTests(String language, String path, String officialRepoUrl) {
        int testsDeduction;
        testsDeduction = evaluationService.executeTests(language, path, officialRepoUrl);

        if (testsDeduction == 255) {
            log.info("test run failed...");
            throw new RuntimeException("Error executing tests");
        } else {
            log.info("tests completed, deduction: " + testsDeduction + " points");
            return testsDeduction;
        }
    }
}
