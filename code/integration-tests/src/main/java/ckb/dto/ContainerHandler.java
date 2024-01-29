package ckb.dto;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ContainerHandler {
    private static final String SCRIPTS_PATH = getScriptsPath();
    private static final int NUM_OF_CONTAINERS = 3;


    public static void start() {
        // Start the containers
        Process process;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(SCRIPTS_PATH + "unix-start-containers.sh").redirectErrorStream(true);
            process = runProcessBuilder(processBuilder);
        } catch (Exception e) {
            ProcessBuilder processBuilder = new ProcessBuilder()
                    .command("powershell", "-Command", "$env:ID='ckb-test'; docker compose up")
                    .redirectErrorStream(true)
                    .directory(new File(SCRIPTS_PATH));
            process = runProcessBuilder(processBuilder);
        }

        // Read the output of the process and check for services started
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            int startedContainers = 0;
            while (startedContainers < NUM_OF_CONTAINERS && (line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("Started AccountManagerApplication")) startedContainers++;
                if (line.contains("Started MailServiceApplication")) startedContainers++;
                if (line.contains("Started GitHubManagerApplication")) startedContainers++;

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stop() throws IOException {
        try {
            new ProcessBuilder(SCRIPTS_PATH + "stop-containers.sh").start();
        } catch (Exception e) {
            new ProcessBuilder()
                    .command("powershell", "-Command", "docker compose stop")
                    .start();
        }
        System.out.println("Containers stopped");
    }

    private static Process runProcessBuilder(ProcessBuilder processBuilder) {
        try {
            return processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getScriptsPath() {
        String path = ContainerHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return path.substring(0, path.indexOf("/integration-tests")) + "/integration-tests/src/test/scripts/";
    }

}
