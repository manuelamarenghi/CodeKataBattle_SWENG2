package ckb.BattleManager.service;

import ckb.BattleManager.model.Participation;
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
        Participation participation = new Participation();
        participation.setStudentId(studentId);
        participation.setTeam(team);
        participationRepository.save(participation);

        log.info("Participation created with studentId {} and teamId {}", studentId, team.getTeamId());
    }

    public void deleteParticipationById(Long studentId, Team team) throws Exception {
        participationRepository
                .delete(
                        participationRepository.findByStudentIdAndTeam(
                                studentId, team
                        ).orElseThrow(
                                () -> {
                                    log.error("Participation not found with studentId {} and teamId {}", studentId, team.getTeamId());
                                    return new Exception("Participation not found with studentId " + studentId + " and teamId " + team.getTeamId());
                                }
                        )
                );

        log.info("Participation deleted with id student {} and id team {}", studentId, team.getTeamId());
    }
}
