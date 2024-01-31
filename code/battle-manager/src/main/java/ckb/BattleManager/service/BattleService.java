package ckb.BattleManager.service;

import ckb.BattleManager.controller.CreateGHRepositoryBattleController;
import ckb.BattleManager.dto.input.CreateBattleRequest;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.repository.BattleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BattleService {
    private final BattleRepository battleRepository;
    private final TeamService teamService;
    private final CreateGHRepositoryBattleController createGHRepositoryBattleController;

    @Autowired

    public BattleService(BattleRepository battleRepository, TeamService teamService,
                         CreateGHRepositoryBattleController createGHRepositoryBattleController) {
        this.battleRepository = battleRepository;
        this.teamService = teamService;
        this.createGHRepositoryBattleController = createGHRepositoryBattleController;
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
                .build();
        battleRepository.save(battle);
        log.info("Battle saved in the database with name {}", battle.getName());

        try {
            String repoLink = createGHRepositoryBattleController.createGHRepository(battle, battleRequest.getFiles());
            battle.setRepositoryLink(repoLink);
            battleRepository.save(battle);
        } catch (Exception e) {
            log.error("Error creating repo in GitHub. Error {}", e.getMessage());
            battleRepository.delete(battle);
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
            if (battle.getSubDeadline().isAfter(LocalDateTime.now())) {
                canClose = false;
                break;
            }
        }

        log.info("The tournament {} can be closed? {}", idTournament, canClose);
        return canClose;
    }

    public List<Pair<Long, Integer>> getAllTeamsOfBattle(Long idBattle) throws Exception {
        Battle battle = getBattle(idBattle);

        return battle.getTeamsRegistered().stream()
                .map(team -> Pair.of(team.getTeamId(), team.getScore()))
                .sorted((p1, p2) -> p2.getRight().compareTo(p1.getRight()))
                .toList();
    }

    public String getOfficialRepo(Long teamId) throws Exception {
        Team team = teamService.getTeam(teamId);
        return team.getBattle().getRepositoryLink();
    }

    public Team getListParticipation(Long battleId, Long studentId) throws Exception {
        Battle battle = getBattle(battleId);
        return teamService.getTeam(battle, studentId);
    }
}
