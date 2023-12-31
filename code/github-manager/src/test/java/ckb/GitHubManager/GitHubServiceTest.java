package ckb.GitHubManager;

import ckb.GitHubManager.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class GitHubServiceTest {

    private final GitHubService gitHubService = new GitHubService();

    @Test
    public void createRepositoryTest() {
        try {
            GHRepository repo = gitHubService.createRepository("test_repo");
            repo.delete();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
