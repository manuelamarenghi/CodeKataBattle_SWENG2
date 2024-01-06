package ckb.BattleManager.service;

import ckb.BattleManager.model.Battle;
import ckb.BattleManager.repository.BattleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Battle getBattle(Long id) {
        return battleRepository.findById(id).orElseThrow(() -> {
            log.info("Battle not found with id: {}", id);
            throw new RuntimeException("");
        });
    }

    public void createBattle(Battle battle) {
        battleRepository.save(battle);
        log.info("Battle created with id: {}", battle.getBattleId());
    }

    public void joinBattle(Long idBattle, Long idStudent) {
        // The student wants to join the battle, create a new team with only the student
        // and register the team to the battle
        teamService.createTeam(idBattle, idStudent);
    }

    public List<Long> getBattleOfTournament(Long idTournament) {
        return battleRepository
                .findBattlesByTournamentId(idTournament)
                .stream()
                .map(Battle::getBattleId)
                .toList();
    }
}
