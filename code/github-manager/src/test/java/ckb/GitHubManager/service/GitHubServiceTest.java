package ckb.GitHubManager.service;

import ckb.GitHubManager.model.WorkingPair;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GitHubServiceTest {
    private final int GENERATE_STRING_LENGTH = 10;
    private final GitHubService gitHubService = new GitHubService();

    @Test
    public void createRepositoryTest() throws IOException {
        GHRepository repo = null;
        try {
            repo = gitHubService.createRepository(getRandomString());
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
        String repoName = getRandomString();
        try {
            repo1 = gitHubService.createRepository(repoName);
            assertNotNull(repo1);
            repo2 = gitHubService.createRepository(repoName);
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
        List<WorkingPair<String, String>> files = List.of(
                new WorkingPair<>("README.md", "This is a test file"),
                new WorkingPair<>("directory/TEST.md", "This is a test file in a directory")
        );
        try {
            repo = gitHubService.createRepository(getRandomString());
            gitHubService.commitAndPush(repo, files);
            checkCorrectness(repo, files);
        } catch (Exception e) {
            fail();
        } finally {
            if (repo != null) repo.delete();
        }
    }

    private void checkCorrectness(GHRepository repo, List<WorkingPair<String, String>> files) throws IOException, InterruptedException {
        Thread.sleep(20000); // wait for GitHub to update

        for (WorkingPair<String, String> file : files) {
            GHContent content;
            content = gitHubService.fetchSources(repo, file.getLeft());
            assertNotNull(content);
            assertEquals(file.getRight(), content.getContent());
        }
    }

    private String getRandomString() {
        return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                .limit(GENERATE_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
