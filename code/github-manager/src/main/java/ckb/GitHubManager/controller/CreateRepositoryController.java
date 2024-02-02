package ckb.GitHubManager.controller;

import ckb.GitHubManager.dto.CreateRepositoryRequest;
import ckb.GitHubManager.model.WorkingPair;
import ckb.GitHubManager.service.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github/create-repo")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CreateRepositoryController extends Controller {

    private final GitHubService gitHubService;

    private static GHRepository lastRepo;
    private boolean debugMode = false;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBattleRepository(@RequestBody CreateRepositoryRequest  request) {
        String repoName = request.getRepoName();
        String repoURL;

        GHRepository repo;
        // create repository
        try{
            repo = gitHubService.createRepository(repoName);
            assert repo != null;
            repoURL = repo.getHtmlUrl().toString();
            log.info("Repository {} created successfully\n", repoName);
        } catch (Exception e){
            log.error("Error while creating repository {}\n", repoName);
            return new ResponseEntity<>("server failed to create repository", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<WorkingPair<String, String>> files = request.getFiles();
        // commit and push files
        try{
            gitHubService.commitAndPush(repo, files);
            log.info("Files committed and pushed successfully\n");
        } catch (Exception e){
            log.error("Error while committing and pushing files to repository {}\n {}", repoName, e.getMessage());
            return new ResponseEntity<>("server failed to commit and push files", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (debugMode) {
            lastRepo = repo;
        }
        return new ResponseEntity<>(repoURL, getHeaders(), HttpStatus.CREATED);
    }

    public void initDebugMode() {
        debugMode = true;
    }

    public GHRepository getLastRepo() {
        return lastRepo;
    }

}
