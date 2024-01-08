package ckb.BattleManager.controller;

import ckb.BattleManager.service.ParticipationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ParticipationController {
    ParticipationService participationService;

    @Autowired
    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    public void registerStudentToTeam(Long idTeam, Long idStudent) {
        log.info("[API REQUEST] Register student to team");
        participationService.registerStudentToTeam(idTeam, idStudent);
    }
}
