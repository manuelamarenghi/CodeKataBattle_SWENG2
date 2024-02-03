package ckb.GitHubManager.service;

import ckb.GitHubManager.model.WorkingPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubService {

    private final String token = "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";

    public GHRepository createRepository(String repoName) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(token).build();
            return new GHCreateRepositoryBuilder(repoName, github, "/user/repos")
                    .autoInit(true) // creates a README.md as a first commit
                    .allowForking(true)
                    .defaultBranch("main")
                    .private_(true)
                    .create();
        } catch (Exception e) {
            log.error("Error while creating repository {}\n", repoName);
            return null;
        }
    }

    public GHRepository getRepo(String repoName) {
        try {
            return GitHub.connectUsingOAuth(token).getRepository(repoName);
        } catch (Exception e) {
            log.error("Error while getting repository {}", repoName);
            throw new RuntimeException("Could not find repository");
        }
    }
    public void makeRepositoryPublic(String repoName) {
        try {
            getRepo(repoName).setVisibility(GHRepository.Visibility.PUBLIC);
        } catch (IOException e) {
            throw new RuntimeException("Could not connect to GitHub");
        }
    }

    public void commitAndPush(GHRepository repo, List<WorkingPair<String, String>> files) throws IOException {

        String lastCommitSHA = getLastCommitSHA(repo);
        String baseTreeSHA = getBaseTreeSHA(repo, lastCommitSHA);
        String newTreeSHA = createNewTree(repo, baseTreeSHA, files);
        String newCommitSHA = createNewCommit(repo, lastCommitSHA, newTreeSHA);

        // forcefully push the new commit to the default branch
        repo.getRef("refs/heads/" + repo.getDefaultBranch()).updateTo(newCommitSHA, true);
    }

    public GHContent fetchSources(GHRepository repo, String filePath) throws IOException {
        GHRef ref = repo.getRef("heads/main");
        GHCommit commit = repo.getCommit(ref.getObject().getSha());
        try {
            return repo.getFileContent(filePath, commit.getSHA1());
        } catch (GHFileNotFoundException e) {
            log.error("file not found for path: {}", filePath);
            return null;
        }
    }

    private String getLastCommitSHA(GHRepository repo) throws IOException {
        return repo.getRef("refs/heads/" + repo.getDefaultBranch())
                .getObject()
                .getSha();
    }

    private String getBaseTreeSHA(GHRepository repo, String lastCommitSHA) throws IOException {
        if (lastCommitSHA == null) {
            log.error("first commit failed");
            throw new RuntimeException("failed to get last commit SHA");
        }
        return repo.getCommit(lastCommitSHA)
                .getTree()
                .getSha();
    }

    private String createNewTree(GHRepository repo, String baseTreeSHA, List<WorkingPair<String, String>> files) throws IOException {
        GHTreeBuilder treeBuilder = repo.createTree().baseTree(baseTreeSHA);
        for (WorkingPair<String, String> file : files) { // add each file to the tree
            treeBuilder.add(
                    file.getLeft(), // path
                    file.getRight(), // content
                    false // is executable
            );
        }
        return treeBuilder.create().getSha();
    }

    private String createNewCommit(GHRepository repo, String lastCommitSHA, String newTreeSHA) throws IOException {
        return repo.createCommit()
                .tree(newTreeSHA)
                .message("second commit")
                .parent(lastCommitSHA)
                .create()
                .getSHA1();
    }

}
