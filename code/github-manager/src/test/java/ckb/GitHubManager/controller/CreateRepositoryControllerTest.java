package ckb.GitHubManager.controller;

import ckb.GitHubManager.dto.CreateRepositoryRequest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CreateRepositoryControllerTest {
    private final int GENERATE_STRING_LENGTH = 10;

    @Autowired
    private CreateRepositoryController createRepositoryController;

    @Test
    public void createRepositoryTest() throws IOException {
        List<ImmutablePair<String, String>> files = new ArrayList<>();
        files.add(new ImmutablePair<>("test_file", "test_content"));

        CreateRepositoryRequest request = CreateRepositoryRequest.builder()
                .repoName(getRandomString())
                .files(files)
                .build();

        createRepositoryController.initDebugMode();
        ResponseEntity<Object> response = createRepositoryController.createBattleRepository(request);
        createRepositoryController.getLastRepo().delete();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        
    }

    private String getRandomString() {
        return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                .limit(GENERATE_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
