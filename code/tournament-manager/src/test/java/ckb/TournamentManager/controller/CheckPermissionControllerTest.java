package ckb.TournamentManager.controller;

import ckb.TournamentManager.model.Permission;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.repo.PermissionRepo;
import ckb.TournamentManager.repo.TournamentRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;

@SpringBootTest
class CheckPermissionControllerTest {

    @Autowired
    private TournamentRepo tournamentRepo;

    @Autowired
    private PermissionRepo permissionRepo;

    @Test
    void checkPermission() {
        tournamentRepo.save(new Tournament(
                1L,
                "Tournament 1",
                new Date(2024 - 1900, Calendar.FEBRUARY, 1),
                true,
                1L
        ));
        permissionRepo.save(new Permission(1L, 1L));
    }
}