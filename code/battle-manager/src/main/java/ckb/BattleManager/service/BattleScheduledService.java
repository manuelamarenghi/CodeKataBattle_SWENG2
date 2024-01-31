package ckb.BattleManager.service;

import ckb.BattleManager.controller.MakeRepositoryPublicController;
import ckb.BattleManager.controller.SendTeamsPointsController;
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
    private final MakeRepositoryPublicController makeRepositoryPublicController;
    private final SendTeamsPointsController sendTeamsPointsController;

    public BattleScheduledService(BattleRepository battleRepository, TeamService teamService,
                                  MakeRepositoryPublicController makeRepositoryPublicController,
                                  SendTeamsPointsController sendTeamsPointsController) {
        this.battleRepository = battleRepository;
        this.teamService = teamService;
        this.makeRepositoryPublicController = makeRepositoryPublicController;
        this.sendTeamsPointsController = sendTeamsPointsController;
    }

    @Scheduled(fixedRate = 3000) // 3 Seconds
    public void startBattles() {
        List<Battle> battlesToStart = battleRepository.findBattlesByHasStartedIsFalseAndRegDeadlineIsBefore(LocalDateTime.now());
        battlesToStart.forEach(battle -> {
            battle.setHasStarted(true);
            battleRepository.save(battle);
            log.info("Starting battle with name: {}", battle.getName());

            try {
                // Call the GitHub manager to start the battle
                // TODO make repo public
                makeRepositoryPublicController.makeRepositoryPublic(battle.getRepositoryLink());
            } catch (Exception e) {
                log.error("Error making the repository public for battle with name {}. Error {}", battle.getName(), e.getMessage());
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
            log.info("The battle {} has ended", battle.getName());

            if (battle.getBattleToEval()) {
                battle.setIsClosed(true);
                log.info("The battle {} has been closed", battle.getName());
            }

            // Get the teams and the points of each battle
            // send the a class containing tournament_id, List<Pair<idTeam, points>>
            // to the Tournament manager
            sendTeamsPointsController.sendIdUsersPointsFinishedBattle(
                    battle,
                    teamService.getListPairIdUserPoints(battle)
            );
        });
    }
}
