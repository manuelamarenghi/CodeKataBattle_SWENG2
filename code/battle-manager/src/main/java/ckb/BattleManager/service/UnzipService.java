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
    public List<WorkingPair<String, String>> unzip(String zipFileName, String randomName) throws IOException {
        Process process;
        String script =
                // create a directory with random name where to unzip the files
                "cd || exit 1\n" +
                        "zipFilePath=" + zipFileName + ";\n" +
                        "mkdir " + randomName + ";\n" +
                        "mv " + zipFileName + " " + randomName + ";\n" +
                        "cd " + randomName + " || exit 1;\n" +

                        // unzip the file and remove the .git folder
                        "zipFileName=\"${zipFilePath##*/}\";\n" +
                        "unzip \"$zipFileName\";\n" +
                        "unzipDir=$(pwd);\n" +
                        "cd \"$unzipDir\" || exit 1;\n" +
                        "rm -rf .git/;\n" +
                        "readarray -t paths < <(find ./ -type f);\n" +

                        "echo;\n" +
                        "echo \"Unzip completed\";\n" +

                        // print the path of each file
                        "for path in \"${paths[@]}\"; do\n" +
                        "\techo \"$path\";\n" +
                        "done\n" +
                        "cd || exit 1;\n" +
                        "exit";
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", script).redirectErrorStream(true);
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
        String script =
                "cd || exit 1;\n" +
                        "rm -rf " + randomName + ";\n" +
                        "exit 0";
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", script).redirectErrorStream(true);
        process = processBuilder.start();
        if (process.waitFor() != 0) throw new RuntimeException("Error while cleaning up");
    }

    private String readFiledContent(String zipFileName, String path, String randomName) throws IOException {
        log.info("Reading file: {} at {} ...", path, zipFileName);
        Process process;

        String script =
                "cd || exit 1;\n" +
                        "cd " + randomName + " || exit 1;\n" +
                        "cat " + path;
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", script).redirectErrorStream(true);
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
}
