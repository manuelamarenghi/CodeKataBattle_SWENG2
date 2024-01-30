package ckb.BattleManager.service;

import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.ParticipationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParticipationService {
    private final ParticipationRepository participationRepository;

    @Autowired
    public ParticipationService(ParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public void createParticipation(Long studentId, Team team) {
        participationRepository.save(
                new Participation(
                        new ParticipationId(
                                studentId, team
                        )
                )
        );
        log.info("Participation created with studentId {} and teamId {}", studentId, team.getTeamId());
    }

    public void deleteParticipationHavingIdStudentAndTeam(Long idStudent, Team team) {
        participationRepository.deleteById(
                new ParticipationId(
                        idStudent, team
                )
        );
        log.info("Participation deleted with studentId {} and teamId {}", idStudent, team.getTeamId());
    }
}
