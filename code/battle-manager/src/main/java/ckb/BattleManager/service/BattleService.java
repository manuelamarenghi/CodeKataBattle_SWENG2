package ckb.BattleManager.service;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BattleService {
    private final BattleRepository battleRepository;
    private final TeamService teamService;

    @Autowired

    public BattleService(BattleRepository battleRepository, TeamService teamService) {
        this.battleRepository = battleRepository;
        this.teamService = teamService;
    }

    public Battle getBattle(Long id) throws Exception {
        return battleRepository.findById(id).orElseThrow(() -> {
            log.info("Battle not found with id: {}", id);
            return new Exception("Battle not found with id: " + id);
        });
    }

    public void createBattle(Battle battle) {
        battleRepository.save(battle);
        log.info("Battle created with id: {}", battle.getBattleId());
    }

    public List<Long> getBattleOfTournament(Long idTournament) {
        return battleRepository
                .findBattlesByTournamentId(idTournament)
                .stream()
                .map(Battle::getBattleId)
                .toList();
    }

    public void joinBattle(Long idStudent, Long idBattle) throws Exception {
        Battle battle = battleRepository.findById(idBattle).orElseThrow(
                () -> {
                    log.info("Battle not found with id: {}", idBattle);
                    return new Exception("Battle not found with id: " + idBattle);
                }
        );
        teamService.createTeam(idStudent, battle);
    }

    public void leaveBattle(Long idStudent, Long idBattle) throws Exception {
        Battle battle = battleRepository.findById(idBattle).orElseThrow(
                () -> {
                    log.info("Battle not found with id: {}", idBattle);
                    return new Exception("Battle not found with id: " + idBattle);
                }
        );
        teamService.deleteParticipation(idStudent, battle);
    }

    public boolean canCloseTournament(Long idTournament) {
        List<Battle> battles = battleRepository.findBattlesByTournamentId(idTournament);
        boolean canClose = true;
        for (Battle battle : battles) {
            if (battle.getSubDeadline().isAfter(LocalDateTime.now())) {
                canClose = false;
                break;
            }
        }
        return canClose;
    }

    public List<Team> getAllTeamsOfBattle(Long idBattle) throws Exception {
        Battle battle = battleRepository.findById(idBattle).orElseThrow(() -> {
            log.info("Battle not found with id: {}", idBattle);
            return new Exception("Battle not found with id: " + idBattle);
        });
        return battle.getTeamsRegistered();
    }
}
