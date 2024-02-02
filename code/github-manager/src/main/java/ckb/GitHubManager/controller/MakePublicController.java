package ckb.GitHubManager.controller;

import ckb.GitHubManager.dto.MakePublicRequest;
import ckb.GitHubManager.service.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github/make-public")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MakePublicController extends Controller {
    private final GitHubService githubService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> makePublic(@RequestBody MakePublicRequest request) {
        try {
            String repoUrl = request.getRepoName();
            log.info("Changing repository visibility for {}", repoUrl);
            String repoName = repoUrl
                    .replace("https://github.com/", "")
                    .replace(".git", "");
            log.info("name: {}", repoName);

            githubService.makeRepositoryPublic(repoName);
            return new ResponseEntity<>("Repository set to public", getHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while changing repository visibility for {}\n", request.getRepoName());
            return new ResponseEntity<>("Failed to change visibility", getHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
