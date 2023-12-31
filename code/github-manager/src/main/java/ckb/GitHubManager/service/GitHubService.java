package ckb.GitHubManager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubService {

    private final String token = "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";

    public GHRepository createRepository(String repoName) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(token).build();

        return new GHCreateRepositoryBuilder(repoName, github, "/user/repos")
                .allowForking(true)
                .defaultBranch("main")
                .private_(false)
                .create();
    }

}
