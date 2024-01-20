package ckb.BattleManager.service;

import ckb.BattleManager.controller.SendTeamsPointsFinishedBattleController;
import ckb.BattleManager.controller.StartBattleController;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.repository.BattleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BattleScheduledService {
    private final BattleRepository battleRepository;
    private final TeamService teamService;
    private final StartBattleController startBattleController;
    private final SendTeamsPointsFinishedBattleController sendTeamsPointsFinishedBattleController;

    public BattleScheduledService(BattleRepository battleRepository, TeamService teamService,
                                  StartBattleController startBattleController,
                                  SendTeamsPointsFinishedBattleController sendTeamsPointsFinishedBattleController) {
        this.battleRepository = battleRepository;
        this.teamService = teamService;
        this.startBattleController = startBattleController;
        this.sendTeamsPointsFinishedBattleController = sendTeamsPointsFinishedBattleController;
    }

    @Scheduled(fixedRate = 3000) // 3 Seconds
    public void startBattles() {
        List<Battle> battlesToStart = battleRepository.findBattlesByHasStartedIsFalse();
        //log.info("Found {} battles to start", battlesToStart.size());
        battlesToStart.forEach(battle -> {
            if (battle.getRegDeadline().isBefore(LocalDateTime.now())) {
                battle.setHasStarted(true);
                battleRepository.save(battle);

                // Call the GitHub manager to start the battle
                startBattleController.startBattle(battle);
            }
        });
    }

    @Scheduled(fixedRate = 3000) // 3 Seconds
    public void closeBattles() {
        List<Battle> battlesToStart = battleRepository.
                findBattlesByHasEndedIsFalseAndSubDeadlineBefore(LocalDateTime.now());
        battlesToStart.forEach(battle -> {
            battle.setHasEnded(true);
            battleRepository.save(battle);

            // Get the teams and the points of each battle
            // send the a class containing tournament_id, List<Team> and List<Integer>
            // to the Tournament manager
            sendTeamsPointsFinishedBattleController.sendIdUsersPointsFinishedBattle(
                    battle,
                    teamService.getListPairIdUserPoints(battle)
            );
        });
    }
}
