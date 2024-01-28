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
public class EvaluateController extends Controller {
    private final EvaluationService evaluationService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> evaluate(@RequestBody String repoUrl) {
        //TODO: pull repo
        String path = evaluationService.pullRepo(repoUrl);
        if (!path.equals("ERR")) {
            log.info("Repository at " + repoUrl + " pulled successfully ");
        } else {
            log.error("Error pulling repository at " + repoUrl);
            return new ResponseEntity<>("Error pulling repo", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        //TODO: compile -> if failed 0 points
        evaluationService.compile();

        //TODO: run tests
        int successfulTests = 0, failedTests = 0;

        // run static analysis
        int staticAnalysisDeduction = evaluationService.executeStaticAnalysis("c", path);
        if (staticAnalysisDeduction < 0) {
            log.error("Error executing static analysis");
            return new ResponseEntity<>("Error executing static analysis", getHeaders(), HttpStatus.BAD_REQUEST);
        } else {
            log.info("Static analysis executed successfully");
            log.info("Deduction: " + staticAnalysisDeduction);
        }

        // calculate score

        return new ResponseEntity<>("Evaluation successful", getHeaders(), HttpStatus.OK);

    }

    boolean compile() {
        return true;
    }
}
