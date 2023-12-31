package ckb.GitHubManager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubService {

    private final String token = "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";

    public GHRepository createRepository(String repoName) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(token).build();
        return new GHCreateRepositoryBuilder(repoName, github, "/user/repos")
                .autoInit(true) // creates a README.md as a first commit
                .allowForking(true)
                .defaultBranch("main")
                .private_(false)
                .create();
    }

    public void commitAndPush(GHRepository repo, List<ImmutablePair<String, String>> files) throws IOException {

        String lastCommitSHA = getLastCommitSHA(repo);
        System.out.println("Last commit SHA: " + lastCommitSHA);

        String baseTreeSHA = getBaseTreeSHA(repo, lastCommitSHA);
        System.out.println("Base tree SHA: " + baseTreeSHA);

        String newTreeSHA = createNewTree(repo, baseTreeSHA, files);
        System.out.println("New tree SHA: " + newTreeSHA);

        String newCommitSHA = createNewCommit(repo, lastCommitSHA, newTreeSHA);
        System.out.println("New commit SHA: " + newCommitSHA);

        // forcefully push the new commit to the default branch
        repo.getRef("refs/heads/" + repo.getDefaultBranch()).updateTo(newCommitSHA, true);
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

    private String createNewTree(GHRepository repo, String baseTreeSHA, List<ImmutablePair<String, String>> files) throws IOException {
        GHTreeBuilder treeBuilder = repo.createTree().baseTree(baseTreeSHA);
        for (ImmutablePair<String, String> file : files) { // add each file to the tree
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
