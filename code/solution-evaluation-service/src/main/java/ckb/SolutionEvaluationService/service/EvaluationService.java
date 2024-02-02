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

    private final int ERROR_DEDUCTION = 30;
    private final int WARNING_DEDUCTION = 5;
    private final int STYLE_DEDUCTION = 2;

    public String pullRepo(String repoUrl) {
        // get repo name
        String name = repoUrl.substring(0, repoUrl.lastIndexOf("."))
                .replace("https://", "")
                .substring(repoUrl.replace("https://", "").indexOf("/") + 1)
                .replace("/", "_");

        String script = "cd || exit 1;\n" +
                "rm -rf " + name + ";" + // delete any repo with the same name that was for some reason left behind
                "git clone " + repoUrl + " " + name + " || exit 1;\n" +
                "cd " + name + " || exit 1;\n" +
                "pwd\n";

        try {
            // start process
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", script).redirectErrorStream(true);
            Process process = processBuilder.start();

            // read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> output = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
                log.info(line);
            }

            if (process.waitFor() == 1) throw new RuntimeException("Error pulling repo: " + repoUrl);
            return output.getLast();
        } catch (Exception e) {
            log.error("Error pulling repo: " + repoUrl + " " + e.getMessage());
            return "ERR";
        }
    }

    public void cleanUp(String path) {
        String script =
                "rm -rf " + path + ";\n" +
                        "echo \"clean up successful\"";
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", script).redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
            reader.close();
            if (process.waitFor() != 0) throw new RuntimeException("Error cleaning up " + path);
        } catch (Exception e) {
            throw new RuntimeException("Error cleaning up " + path);
        }
    }

    public int calculateDeduction(List<String> output) {
        int totalDeduction = 0;
        totalDeduction += Integer.valueOf(output.get(output.indexOf("errors") + 1)) * ERROR_DEDUCTION;
        totalDeduction += Integer.valueOf(output.get(output.indexOf("warnings") + 1)) * WARNING_DEDUCTION;
        totalDeduction += Integer.valueOf(output.get(output.indexOf("style") + 1)) * STYLE_DEDUCTION;
        return totalDeduction;
    }
}
