package ckb.SolutionEvaluationService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationService {

    private final String SCRIPTS_PATH = getScriptsPath();

    private final int ERROR_DEDUCTION = 30;
    private final int WARNING_DEDUCTION = 5;
    private final int STYLE_DEDUCTION = 2;

    public String pullRepo(String repoUrl) {
        String script = SCRIPTS_PATH + "pull-repo.sh";

        try {
            String name = repoUrl.substring(0, repoUrl.lastIndexOf("."))
                    .replace("https://", "")
                    .substring(repoUrl.replace("https://", "").indexOf("/") + 1)
                    .replace("/", "_");
            ProcessBuilder processBuilder = new ProcessBuilder(script, repoUrl, name).redirectErrorStream(true);
            List<String> output = new ArrayList<>();
            try {
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
            } catch (Exception e) {
                log.error("Error pulling repo: " + e.getMessage());
                return "ERR";
            }
            return output.getLast();
        } catch (Exception e) {
            log.error("Error pulling repo: " + e.getMessage());
            return "ERR";
        }
    }

    public int executeStaticAnalysis(String language, String path) {
        String script = SCRIPTS_PATH + "analysis/" + language + "-static-analysis.sh";
        ProcessBuilder processBuilder = new ProcessBuilder(script, path).redirectErrorStream(true);
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
        return calculateDeduction(output);
    }

    public boolean compile(String language, String path) {
        String script = SCRIPTS_PATH + "compilation/" + language + "-compiling.sh";
        ProcessBuilder processBuilder = new ProcessBuilder(script, path).redirectErrorStream(true);
        try {
            return processBuilder.start().waitFor() == 0;
        } catch (Exception e) {
            log.error("Internal error occurred during compilation at " + path + ": " + e.getMessage());
            return false;
        }
    }

    public int executeTests(String language, String path) {
        String script = SCRIPTS_PATH + "test-execution/" + language + "-test-execution.sh";
        ProcessBuilder processBuilder = new ProcessBuilder(script, path).redirectErrorStream(true);
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

    private int calculateDeduction(List<String> output) {
        int totalDeduction = 0;
        totalDeduction += Integer.valueOf(output.get(output.indexOf("errors") + 1)) * ERROR_DEDUCTION;
        totalDeduction += Integer.valueOf(output.get(output.indexOf("warnings") + 1)) * WARNING_DEDUCTION;
        totalDeduction += Integer.valueOf(output.get(output.indexOf("style") + 1)) * STYLE_DEDUCTION;
        return totalDeduction;
    }

    private static String getScriptsPath() {
        String path = EvaluationService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return path.substring(0, path.indexOf("/solution-evaluation-service")) + "/solution-evaluation-service/src/main/scripts/";
    }
}
