package ckb.GitHubManager.controller;

import ckb.GitHubManager.dto.CreateRepositoryRequest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CreateRepositoryControllerTest {

    @Autowired
    private CreateRepositoryController createRepositoryController;

    @Test
    public void createRepositoryTest() throws IOException {
        List<ImmutablePair<String, String>> files = new ArrayList<>();
        files.add(new ImmutablePair<>("test_file", "test_content"));

        CreateRepositoryRequest request = CreateRepositoryRequest.builder()
                .repoName("test_repo")
                .files(files)
                .build();

        createRepositoryController.initDebugMode();
        ResponseEntity<Object> response = createRepositoryController.createBattleRepository(request);
        createRepositoryController.getLastRepo().delete();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        
    }
}
