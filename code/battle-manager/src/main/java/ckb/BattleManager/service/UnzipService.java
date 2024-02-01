package ckb.BattleManager.service;

import ckb.BattleManager.model.WorkingPair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class UnzipService {
    private final String SCRIPTS_PATH = getScriptsPath();

    public List<WorkingPair<String, String>> unzip(String zipFileName, String randomName) throws IOException {
        Process process;
        String script = SCRIPTS_PATH + "unzip.sh";
        ProcessBuilder processBuilder = new ProcessBuilder(script, zipFileName, randomName
        ).redirectErrorStream(true);
        process = processBuilder.start();

        List<WorkingPair<String, String>> files = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Unzip completed")) break;
            }
            log.info("Unzip completed for {}", zipFileName);
            while (true) {
                String path = reader.readLine();
                if (path == null) break;
                path = path.replace("./", "");
                String content = readFiledContent(zipFileName.replace(".zip", ""), path, randomName
                );
                files.add(new WorkingPair<>(path, content));
            }
            log.info("Read all files for {}", zipFileName);
            cleanUp(randomName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to unzip files");
        }
        return files;
    }

    private void cleanUp(String randomName) throws IOException, InterruptedException {
        Process process;
        String script = SCRIPTS_PATH + "destroyer.sh";
        ProcessBuilder processBuilder = new ProcessBuilder(script, randomName).redirectErrorStream(true);
        process = processBuilder.start();
        if (process.waitFor() != 0) throw new RuntimeException("Error while cleaning up");
    }

    private String readFiledContent(String zipFileName, String path, String randomName) throws IOException {
        log.info("Reading file: {} at {} ...", path, zipFileName);
        Process process;
        String script = SCRIPTS_PATH + "read-file.sh";
        ProcessBuilder processBuilder = new ProcessBuilder(script, zipFileName, path, randomName
        ).redirectErrorStream(true);
        process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            log.info("OK");
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("FAILED");
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getScriptsPath() {
        String path = UnzipService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return path.substring(0, path.indexOf("/battle-manager")) + "/battle-manager/src/main/scripts/";
    }
}
