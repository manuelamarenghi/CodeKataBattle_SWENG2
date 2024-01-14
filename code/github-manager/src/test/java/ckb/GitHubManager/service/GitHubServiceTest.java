package ckb.GitHubManager.service;

import ckb.GitHubManager.service.GitHubService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GitHubServiceTest {

    private final GitHubService gitHubService = new GitHubService();


    @Test
    public void createRepositoryTest() throws IOException {
        GHRepository repo = null;
        try {
            repo = gitHubService.createRepository("test_repo");
            assertNotNull(repo);
        } catch (Exception e) {
            fail();
        } finally {
            if (repo != null) repo.delete();
        }
    }

    @Test
    public void createDuplicateRepositoryTest() throws IOException {
        GHRepository repo1 = null;
        GHRepository repo2 = null;
        try {
            repo1 = gitHubService.createRepository("test_repo");
            assertNotNull(repo1);
            repo2 = gitHubService.createRepository("test_repo");
            assertNull(repo2);
        } catch (Exception e) {
            fail();
        } finally {
            if (repo1 != null) repo1.delete();
            if (repo2 != null) repo2.delete();
        }
    }

    @Test
    public void firstCommitTest() throws IOException {
        GHRepository repo = null;
        List<ImmutablePair<String, String>> files = List.of(
                new ImmutablePair<>("README.md", "This is a test file"),
                new ImmutablePair<>("directory/TEST.md", "This is a test file in a directory")
        );
        try {
            repo = gitHubService.createRepository("test_repo");
            gitHubService.commitAndPush(repo, files);
            checkCorrectness(repo, files);
        } catch (Exception e) {
            fail();
        } finally {
            if (repo != null) repo.delete();
        }
    }

    private void checkCorrectness(GHRepository repo, List<ImmutablePair<String, String>> files) throws IOException {
        for (ImmutablePair<String, String> file : files) {
            GHContent content;
            content = gitHubService.fetchSources(repo, file.getLeft());
            assertNotNull(content);
            assertEquals(file.getRight(), content.getContent());
        }
    }
}
