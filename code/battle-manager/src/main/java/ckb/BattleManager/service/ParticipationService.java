package ckb.BattleManager.service;

import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.ParticipationId;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.ParticipationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService {
    ParticipationRepository participationRepository;

    @Autowired
    public ParticipationService(ParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public void registerStudentToTeam(Long idTeam, Long idStudent) {
        // delete the record in participation and if the team has 0 members
        // delete the team, also insert a new line in participation

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

    public void deleteParticipation(Long idStudent, Long idBattle) {
        // delete the record in participation
        participationRepository.deleteById(
                new ParticipationId(
                        idStudent, participationRepository.findTeamByBattleIdAndStudentId(idStudent, idBattle)
                )
        );
    }
}
