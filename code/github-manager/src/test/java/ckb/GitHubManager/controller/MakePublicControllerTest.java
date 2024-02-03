package ckb.GitHubManager.controller;

import ckb.GitHubManager.dto.CreateRepositoryRequest;
import ckb.GitHubManager.dto.MakePublicRequest;
import ckb.GitHubManager.model.WorkingPair;
import ckb.GitHubManager.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class MakePublicControllerTest {
    private final int GENERATE_STRING_LENGTH = 20;

    @Autowired
    private MakePublicController makePublicController;
    @Autowired
    private CreateRepositoryController createRepositoryController;
    GitHubService githubService = new GitHubService();

    @Test
    public void changeVisibilityTest() throws IOException {
        String repoName = getRandomString();
        CreateRepositoryRequest createRequest = CreateRepositoryRequest.builder()
                .repoName(repoName)
                .files(List.of(new WorkingPair<>("README.md", "This is a test")))
                .build();
        createRepositoryController.createBattleRepository(createRequest);
        String nameFromUrl = "https://github.com/Code-Kata-Battle/" + repoName + ".git";

        System.out.println(nameFromUrl);

        String fullName = nameFromUrl.replace("https://github.com/", "").replace(".git", "");
        // full name should just be Code-Kata-Battle/<random string>
        assertEquals(githubService.getRepo(fullName).getVisibility(), GHRepository.Visibility.PRIVATE);

        MakePublicRequest makePublicRequest = MakePublicRequest.builder()
                .repoName(nameFromUrl)
                .build();
        makePublicController.makePublic(makePublicRequest);

        assertEquals(githubService.getRepo(fullName).getVisibility(), GHRepository.Visibility.PUBLIC);

        githubService.getRepo(fullName).delete();
    }

    @Test
    public void nonExistingRepoTest() throws IOException {
        String repoName = getRandomString();
        CreateRepositoryRequest createRequest = CreateRepositoryRequest.builder()
                .repoName(repoName)
                .files(List.of(new WorkingPair<>("README.md", "This is a test")))
                .build();
        createRepositoryController.createBattleRepository(createRequest);
        String nameFromUrl = "SomeWrongName";

        MakePublicRequest makePublicRequest = MakePublicRequest.builder()
                .repoName(nameFromUrl)
                .build();

        ResponseEntity<Object> response = makePublicController.makePublic(makePublicRequest);
        assertFalse(response.getStatusCode().is2xxSuccessful());

        githubService.getRepo("Code-kata-Battle/" + repoName).delete();
    }


    private String getRandomString() {
        return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                .limit(GENERATE_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
