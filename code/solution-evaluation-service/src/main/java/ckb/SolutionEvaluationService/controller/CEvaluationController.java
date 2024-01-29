package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solution-evaluation/c")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CEvaluationController extends Controller {
    private final EvaluationService evaluationService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> evaluate(@RequestBody String repoUrl) {
        // pull repo
        String path = evaluationService.pullRepo(repoUrl);
        if (!path.equals("ERR")) {
            log.info("Repository at " + repoUrl + " pulled successfully ");
        } else {
            log.error("Error pulling repository at " + repoUrl);
            return new ResponseEntity<>("Error pulling repo", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        // compile -> if failed 0 points
        if (!evaluationService.compile("c", path)) {
            log.error("Compilation failed");
            return new ResponseEntity<>("Compilation failed", getHeaders(), HttpStatus.BAD_REQUEST);
        } else {
            log.info("Compilation successful");
        }

        // run tests
        int testsDeduction;
        try {
            testsDeduction = evaluationService.executeTests("c", path);
        } catch (Exception e) {
            log.error("Error executing tests");
            return new ResponseEntity<>("Error executing tests", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (testsDeduction < 0) {
            log.error("Error executing tests");
            return new ResponseEntity<>("Error executing tests", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else if (testsDeduction == 255) {
            log.info("test run failed...");
            return new ResponseEntity<>("Error executing tests", getHeaders(), HttpStatus.BAD_REQUEST);
        } else {
            log.info("tests completed, deduction: " + testsDeduction + " points");
        }

        if (testsDeduction > 0) {
            log.info("Tests failed, no further evaluation");
            return new ResponseEntity<>("Tests failed, no further evaluation... you suck", getHeaders(), HttpStatus.OK);
        }

        // run static analysis
        int staticAnalysisDeduction = evaluationService.executeStaticAnalysis("c", path);
        if (staticAnalysisDeduction < 0) {
            log.error("Error executing static analysis");
            return new ResponseEntity<>("Error executing static analysis", getHeaders(), HttpStatus.BAD_REQUEST);
        } else {
            log.info("Deduction: " + staticAnalysisDeduction);
        }

        // calculate score
        int score = 100 - testsDeduction - staticAnalysisDeduction;
        log.info("Evaluation completed for " + repoUrl + " score: " + score);
        if (score < 0) return new ResponseEntity<>("Too many mistakes were made, please get better...", getHeaders(), HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>("Evaluation successful", getHeaders(), HttpStatus.OK);
    }

}
