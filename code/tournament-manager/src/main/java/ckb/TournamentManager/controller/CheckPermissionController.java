package ckb.TournamentManager.controller;

import ckb.TournamentManager.dto.incoming.CheckPermissionRequest;
import ckb.TournamentManager.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/tournament/check-permission")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class CheckPermissionController {
    @Autowired
    private TournamentService tournamentService;

    @PostMapping
    public ResponseEntity<Object> checkPermission(@RequestBody CheckPermissionRequest request) {
        log.info("Checking permission Tournament: {} ; EducatorId: {}", request.getTournamentId(), request.getEducatorId());

        if (tournamentService.permissionExists(request.getTournamentId(), request.getEducatorId())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
