package ckb.BattleManager.service;

import ckb.BattleManager.dto.output.CreateRepositoryRequest;
import ckb.BattleManager.model.WorkingPair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UnzipServiceTest {
    @Autowired
    private UnzipService unzipService;
    private final WebClient webClient = WebClient.create();
    private final int STRING_LENGTH = 20;

    @Test
    @Disabled
    // can only work if a repo.zip is in the home directory
    // this method should only be tested manually and locally, otherwise it will fail
    // files will be put in the home directory automatically by rest controllers when the application is running
    public void unzipTest() throws IOException {
        List<WorkingPair<String, String>> files = unzipService.unzip("repo.zip", getRandomString());

        CreateRepositoryRequest repoRequest = CreateRepositoryRequest.builder()
                .files(files)
                .repoName(getRandomString())
                .build();

        ResponseEntity<String> response = webClient.post()
                .uri("http://localhost:8083/api/github/create-repo")
                .bodyValue(repoRequest)
                .retrieve()
                .toEntity(String.class)
                .block();

        assert response != null;
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }


    private String getRandomString() {
        return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                .limit(STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
