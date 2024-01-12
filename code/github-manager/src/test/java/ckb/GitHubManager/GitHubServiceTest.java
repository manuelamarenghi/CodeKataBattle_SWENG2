package ckb.GitHubManager;

import ckb.GitHubManager.service.GitHubService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class GitHubServiceTest {

    private final GitHubService gitHubService = new GitHubService();

    @Test
    public void createRepositoryTest() throws IOException {
        GHRepository repo = null;
        try {
            repo = gitHubService.createRepository("test_repo");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (repo != null) repo.delete();
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
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (repo != null) repo.delete();
        }
    }

    @Test
    public void fetchSourcesTest() throws IOException {
        GHRepository repo = null;
        List<ImmutablePair<String, String>> files = List.of(
                new ImmutablePair<>("README.md", "This is a test file"),
                new ImmutablePair<>("directory/TEST.md", "This is a test file in a directory")
        );
        try {
            GHContent content;
            repo = gitHubService.createRepository("test_repo");
            gitHubService.commitAndPush(repo, files);

            content = gitHubService.fetchSources(repo, "README.md");
            assert content != null;
            assertEquals("This is a test file", content.getContent());

            content = gitHubService.fetchSources(repo, "directory/TEST.md");
            assert content != null;
            assertEquals("This is a test file in a directory", content.getContent());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (repo != null) repo.delete();
        }

    }
}
