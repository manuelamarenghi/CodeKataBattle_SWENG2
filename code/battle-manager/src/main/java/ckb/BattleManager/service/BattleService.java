package ckb.BattleManager.service;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.repository.BattleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
            return new Exception("");
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

    public Optional<Battle> findBattleById(Long id) {
        return battleRepository
                .findById(id);
    }

    public void joinBattle(Long idStudent, Long idBattle) throws Exception {
        teamService.createTeam(idStudent, idBattle);
    }

    public void leaveBattle(Long idStudent, Long idBattle) throws Exception {
        teamService.deleteParticipation(idStudent, idBattle);
    }

    public boolean canCloseTournament(Long idTournament) {
        List<Battle> battles = battleRepository.findBattlesByTournamentId(idTournament);
        boolean canClose = true;
        for (Battle battle : battles) {
            if (battle.getSubDeadline().isAfter(LocalDateTime.now())) {
                canClose = false;
            }
        }
        return canClose;
    }
}
