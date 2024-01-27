package ckb.BattleManager.service;

import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
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
    }

    // Delete participation and the team if the team has 0 members
    public void deleteParticipationHavingIdBattle(Long idStudent, Team team) {
        participationRepository.delete(
                new Participation(
                        new ParticipationId(
                                idStudent, team
                        )
                )
        );
    }

    public void deleteParticipationHavingIdTeam(Long idStudent, Team team) {
        participationRepository.deleteById(
                new ParticipationId(
                        idStudent, team
                )
        );
    }
}
