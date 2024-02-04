package ckb.SolutionEvaluationService.controller;

import ckb.SolutionEvaluationService.dto.in.EvaluationParamsResponse;
import ckb.SolutionEvaluationService.dto.in.EvaluationRequest;
import ckb.SolutionEvaluationService.dto.out.AssignScoreRequest;
import ckb.SolutionEvaluationService.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/solution-evaluation/c")
@Slf4j
@RequiredArgsConstructor
public class CEvaluationController extends Controller {
    private final EvaluationService evaluationService;
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> evaluate(@RequestBody EvaluationRequest request) {
        String repoUrl = request.getRepoUrl();
        if (repoUrl == null) {
            log.error("Received evaluation request with null repoUrl");
            return new ResponseEntity<>("Received evaluation request with null repoUrl", getHeaders(), HttpStatus.BAD_REQUEST);
        }
        log.info("received evaluation request for " + repoUrl);

        // pull repo
        String path = evaluationService.pullRepo(repoUrl);
        if (!path.equals("ERR")) {
            log.info("Repository at " + repoUrl + " pulled successfully ");
        } else {
            log.error("Error pulling repository at " + repoUrl);
            return new ResponseEntity<>("Error pulling repo", getHeaders(), HttpStatus.BAD_REQUEST);
        }

        // compile
        if (!compile(path)) {
            log.error("Compilation failed");
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Compilation failed", getHeaders(), HttpStatus.BAD_REQUEST);
        } else {
            log.info("Compilation successful");
        }

        // run tests
        int testsDeduction;
        EvaluationParamsResponse evaluationParams;
        try {
            evaluationParams = getOfficialRepoUrl(request);
            testsDeduction = runTests(path, evaluationParams.getRepoLink());
        } catch (Exception e) {
            if (e.getMessage().equals("Repo is private, battle not started yet")) {
                log.error("Cannot clone a private repository");
                evaluationService.cleanUp(path);
                return new ResponseEntity<>("Official repository is private, battle not started yet", getHeaders(), HttpStatus.BAD_REQUEST);
            }
            log.error("Error executing tests: " + e.getMessage());
            if (e.getMessage().contains("/api/battle/evaluation-params")) {
                log.error("Error getting official repo url, maybe the battle hasn't started yet ?");
                return new ResponseEntity<>("Cannot find official repository for testing, try again later", getHeaders(), HttpStatus.BAD_REQUEST);
            }
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Error executing tests", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (testsDeduction >= 100) {
            log.info("Tests failed, deduction >= 100 points, exiting...");
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Tests failed, 0 points", getHeaders(), HttpStatus.OK);
        }


        // run static analysis
        int staticAnalysisDeduction = executeStaticAnalysis(path, evaluationParams);
        if (staticAnalysisDeduction < 0) {
            log.error("Error executing static analysis");
            evaluationService.cleanUp(path);
            return new ResponseEntity<>("Error executing static analysis", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
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
        try {
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

    private int runTests(String path, String officialRepoUrl) {
        int testsDeduction;
        testsDeduction = executeTests(path, officialRepoUrl);

        if (testsDeduction == 255) {
            log.info("test run failed...");
            throw new RuntimeException("Error executing tests");
        } else {
            log.info("tests completed, deduction: " + testsDeduction + " points");
            return testsDeduction;
        }
    }

    int executeTests(String path, String officialRepoUrl) {
        String script =
                "DEDUCTION_PER_FAIL=20;\n" +
                        // cd into cloned repo directory
                        "cd " + path + " || exit 255;\n" +

                        // clone the official repo to get the official tests
                        "rm -rf tests/;\n" + // have to remove any tests/ directories
                        "git clone " + officialRepoUrl + " \"official_repo\" || exit 255;\n" +
                        "readarray -t officialTestDir < <(find . -wholename \"./official_repo/tests\");\n" +
                        "if (( ${#officialTestDir[@]} == 0 )); then\n" +
                        "    echo \"No tests found in official repo, exiting...\";\n" +
                        "    exit 255;\n" +
                        "fi\n" +
                        "mv \"official_repo/tests\" \"tests\";\n" +

                        // check number of tests
                        "readarray -t inputFiles < <(find . -type f -name 'input_*');\n" +
                        "readarray -t outputFiles < <(find . -type f -name 'output_*');\n" +
                        "if (( ${#inputFiles[@]} != ${#outputFiles[@]} )); then\n" +
                        "    echo \"Number of input files does not match number of output files, exiting...\";\n" +
                        "    exit 255;\n" +
                        "fi\n" +

                        // run all tests and count results
                        "passedTests=0;\n" +
                        "failedTests=0;\n" +
                        "for (( i=1; i<=${#inputFiles[@]}; i++ )); do\n" +
                        "    echo \"Running test case $i\";\n" +
                        "    ./executable < tests/input_\"$i\".txt > output;\n" +
                        "    diff output tests/output_\"$i\".txt > difference;\n" +
                        "    if [ ! -s \"difference\" ]; then\n" +
                        "      passedTests=$((passedTests + 1));\n" +
                        "    else\n" +
                        "      echo \"Test case $i failed\";\n" +
                        "      failedTests=$((failedTests + 1));\n" +
                        "    fi\n" +
                        "done\n" +

                        // calculate the deduction and return it
                        "totalDeduction=$((failedTests * DEDUCTION_PER_FAIL));\n" +
                        "if (( totalDeduction > 100 )); then\n" +
                        "    exit 100;\n" +
                        "fi\n" +
                        "exit $totalDeduction";
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", script).redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
            reader.close();
            return process.waitFor();
        } catch (Exception e) {
            log.error("Error executing tests: " + e.getMessage());
            return -1;
        }
    }

    int executeStaticAnalysis(String path, EvaluationParamsResponse evaluationParams) {
        String script =
                "cd " + path + " || (echo \"failed to cd into working directory\"; exit);\n" +
                        "cppcheck --xml --enable=all *.c &> \"error-log\";\n" +
                        "echo \"errors\";\n" +
                        "grep -E -cc '(severity=\"error\")' \"error-log\";\n" +
                        "echo \"warnings\";\n" +
                        "grep -E -cc '(severity=\"warning\")' \"error-log\";\n" +
                        "echo \"style\";\n" +
                        "grep -E -cc '(severity=\"style\")' \"error-log\";\n";

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", script).redirectErrorStream(true);
        Process process;
        List<String> output = new ArrayList<>();
        try {
            process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
            reader.close();
            log.info("analysis completed, calculating points deduction...");
        } catch (Exception e) {
            log.error("Error executing static analysis: " + e.getMessage());
            return -1;
        }
        return evaluationService.calculateDeduction(output, evaluationParams);
    }

    boolean compile(String path) { // doesn't support multiple files, only compiles *.c
        String script =
                "cd " + path + " || exit 1\n" +
                        "gcc -o executable *.c -O2\n" +
                        "readarray -t array < <(find . -type f -name \"executable\")\n" +
                        "if (( ${#array[@]} )); then\n" +
                        "    echo \"Compilation successful\"\n" +
                        "    exit 0\n" +
                        "else\n" +
                        "    echo \"Compilation error\"\n" +
                        "    exit 1\n" +
                        "fi";

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", script).redirectErrorStream(true);
        try {
            return processBuilder.start().waitFor() == 0;
        } catch (Exception e) {
            log.error("Internal error occurred during compilation at " + path + ": " + e.getMessage());
            return false;
        }
    }

}
