package ckb.BattleManager.service;

import ckb.BattleManager.model.WorkingPair;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class UnzipService {
    private final String SCRIPTS_PATH = getScriptsPath();

    public List<WorkingPair<String, String>> unzip(String zipFileName) throws IOException {
        Process process;
        String script = SCRIPTS_PATH + "unzip.sh";
        ProcessBuilder processBuilder = new ProcessBuilder(script, zipFileName).redirectErrorStream(true);
        process = processBuilder.start();

        List<WorkingPair<String, String>> files = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Unzip completed")) break;
            }

            while (true) {
                String path = reader.readLine();
                if (path == null) break;
                path = path.replace("./", "");
                String content = readFiledContent(zipFileName.replace(".zip", ""), path);
                files.add(new WorkingPair<>(path, content));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to unzip files");
        }
        return files;
    }

    private String readFiledContent(String zipFileName, String path) throws IOException {
        Process process;
        String script = SCRIPTS_PATH + "read-file.sh";
        ProcessBuilder processBuilder = new ProcessBuilder(script, zipFileName, path).redirectErrorStream(true);
        process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getScriptsPath() {
        String path = UnzipService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return path.substring(0, path.indexOf("/battle-manager")) + "/battle-manager/src/main/scripts/";
    }
}
