package ckb.BattleManager.service;

import ckb.BattleManager.controller.CreateGHRepositoryBattleController;
import ckb.BattleManager.controller.SendTeamsPointsController;
import ckb.BattleManager.dto.input.CreateBattleRequest;
import ckb.BattleManager.dto.output.EvaluationParamsResponse;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.model.WorkingPair;
import ckb.BattleManager.repository.BattleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class BattleService {
    private final BattleRepository battleRepository;
    private final TeamService teamService;
    private final CreateGHRepositoryBattleController createGHRepositoryBattleController;
    private final SendTeamsPointsController sendTeamsPointsController;

    @Autowired

    public BattleService(BattleRepository battleRepository, TeamService teamService,
                         CreateGHRepositoryBattleController createGHRepositoryBattleController,
                         SendTeamsPointsController sendTeamsPointsController) {
        this.battleRepository = battleRepository;
        this.teamService = teamService;
        this.createGHRepositoryBattleController = createGHRepositoryBattleController;
        this.sendTeamsPointsController = sendTeamsPointsController;
    }

    public Battle getBattle(Long id) throws Exception {
        return battleRepository.findById(id).orElseThrow(() -> {
            log.error("Battle not found with id: {}", id);
            return new Exception("Battle not found with id: " + id);
        });
    }

    public Battle createBattle(CreateBattleRequest battleRequest) throws Exception {
        Battle battle = Battle.builder()
                .tournamentId(battleRequest.getTournamentId())
                .name(battleRequest.getName())
                .authorId(battleRequest.getAuthorId())
                .minStudents(battleRequest.getMinStudents())
                .maxStudents(battleRequest.getMaxStudents())
                .battleToEval(battleRequest.getBattleToEval())
                .regDeadline(battleRequest.getRegDeadline())
                .subDeadline(battleRequest.getSubDeadline())
                .hasStarted(false)
                .hasEnded(false)
                .isClosed(false)
                .security(battleRequest.getSecurity())
                .reliability(battleRequest.getReliability())
                .maintainability(battleRequest.getMaintainability())
                .build();
        log.info("Battle built: {}", battle);

        try {
            String repoLink = createGHRepositoryBattleController
                    .createGHRepository(battle, battleRequest.getFiles());
            battle.setRepositoryLink(repoLink);
            battleRepository.save(battle);
            log.info("Battle saved in the database: {}", battle);
        } catch (Exception e) {
            log.error("Error creating repo in GitHub, the battle {} will not saved in the database. Error {}", battle.getName(), e.getMessage());
            throw new Exception("Error creating repo in GitHub");
        }

        return battle;
    }

    public List<Long> getBattlesTournament(Long idTournament) {
        return battleRepository
                .findBattlesByTournamentId(idTournament)
                .stream()
                .map(Battle::getBattleId)
                .toList();
    }

    public void joinBattle(Long idStudent, Long idBattle) throws Exception {
        Battle battle = getBattle(idBattle);

        if (LocalDateTime.now().isAfter(battle.getRegDeadline())) {
            log.error("The registration deadline has passed");
            throw new Exception("The registration deadline has passed");
        }

        List<Long> listStudentIdSubscribedToBattle = battle.getTeamsRegistered()
                .stream()
                .flatMap(team -> team.getParticipation().stream())
                .map(Participation::getStudentId)
                .toList();

        if (listStudentIdSubscribedToBattle.contains(idStudent)) {
            log.error("The student {} is already registered in the battle {}", idStudent, idBattle);
            throw new Exception("The student is already registered in the battle");
        }

        teamService.createTeam(idStudent, battle);
    }

    public void leaveBattle(Long idStudent, Long idBattle) throws Exception {
        Battle battle = getBattle(idBattle);
        teamService.deleteParticipation(idStudent, battle);
    }

    public boolean canCloseTournament(Long idTournament) {
        List<Battle> battles = battleRepository.findBattlesByTournamentId(idTournament);
        boolean canClose = true;
        for (Battle battle : battles) {
            if (!battle.getIsClosed()) {
                canClose = false;
                break;
            }
        }

        log.info("The tournament {} can be closed? {}", idTournament, canClose);
        return canClose;
    }

    public List<WorkingPair<Long, Integer>> getAllTeamsOfBattle(Long idBattle) throws Exception {
        Battle battle = getBattle(idBattle);

        Comparator<WorkingPair<Long, Integer>> scoreComparator = Comparator.comparingInt(WorkingPair::getRight);

        return battle.getTeamsRegistered().stream()
                .filter(team -> team.getCanParticipateToBattle().equals(true))
                .map(team -> new WorkingPair<>(team.getTeamId(), team.getScore()))
                .sorted(scoreComparator.reversed())
                .toList();
    }

    public EvaluationParamsResponse getBattleParams(Long teamId) throws Exception {
        Team team = teamService.getTeam(teamId);
        if (!team.getBattle().getHasStarted()) {
            log.error("The battle {} has not started yet", team.getBattle().getName());
            throw new RuntimeException("The battle " + team.getBattle().getName() + " has not started");
        }

        Battle battle = team.getBattle();
        return EvaluationParamsResponse.builder()
                .repoLink(battle.getRepositoryLink())
                .security(battle.getSecurity())
                .reliability(battle.getReliability())
                .maintainability(battle.getMaintainability())
                .build();
    }

    public Team getListParticipation(Long battleId, Long studentId) throws Exception {
        Battle battle = getBattle(battleId);
        return teamService.getTeam(battle, studentId);
    }

    public Battle closeBattle(Long battleId, Long educatorId) throws Exception {
        Battle battle = getBattle(battleId);

        if (battle.getHasEnded().equals(false)) {
            log.error("The battle {} cannot be closed because it has not ended", battle.getName());
            throw new Exception("The battle " + battle.getName() + " has not ended");
        }

        if (battle.getIsClosed().equals(true)) {
            log.error("The battle {} is already closed", battle.getName());
            throw new Exception("The battle " + battle.getName() + " is already closed");
        }

        List<Team> teamsRegistered = battle.getTeamsRegistered();
        for (Team team : teamsRegistered) {
            if (team.getCanParticipateToBattle().equals(true) && team.getEduEvaluated().equals(false)) {
                log.error("The battle {} cannot be closed because the team {} has not been evaluated", battle.getName(), team.getTeamId());
                throw new Exception("The battle " + battle.getName() + " cannot be closed because the team " + team.getTeamId() + " has not been evaluated");
            }
        }

        if (educatorId.equals(battle.getAuthorId())) {
            battle.setIsClosed(true);
            battleRepository.save(battle);
            log.info("Battle {} is closed", battle.getName());
        } else {
            log.error("The educator {} is not the author of the battle {}", educatorId, battle.getName());
            throw new Exception("The educator " + educatorId + " is not the author of the battle " + battle.getName());
        }
        return battle;
    }

    public List<Long> getBattleParticipants(Battle battle) {
        return teamService.getBattleParticipants(battle);
    }
}
