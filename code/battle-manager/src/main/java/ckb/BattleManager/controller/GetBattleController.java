package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.GetBattleRequest;
import ckb.BattleManager.dto.output.BattleInfoMessage;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.service.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/battle")
public class GetBattleController {
    private final BattleService battleService;

    @Autowired
    public GetBattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    /**
     * Method to get a battle
     *
     * @param request id of the battle
     * @return a ResponseEntity with the battle or a not found status
     */
    @GetMapping("/get-battle")
    public ResponseEntity<BattleInfoMessage> getBattle(@RequestBody GetBattleRequest request) {
        log.info("[API REQUEST] Get battle request with id: {}", request.getBattleId());

        try {
            // TODO: dto with manu?
            Battle battle = battleService.getBattle(request.getBattleId());
            Hibernate.initialize(battle.getTeamsRegistered());

            List<Pair<Long, Integer>> pairsIdTeamPoints = battle.getTeamsRegistered().stream()
                    .map(team -> Pair.of(team.getTeamId(), team.getScore()))
                    .sorted((p1, p2) -> p2.getRight().compareTo(p1.getRight()))
                    .toList();

            return ResponseEntity.ok(new BattleInfoMessage(pairsIdTeamPoints));
        } catch (Exception e) {
            log.info("[EXCEPTION] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Method to get all the battles of a tournament
     * ATTENTION: I cannot control the id of the tournament
     *
     * @param idTournament id of the tournament
     * @return a ResponseEntity with the list of ids of the battles
     */
    @GetMapping("/get-battles-tournament")
    public ResponseEntity<List<Long>> getBattlesOfTournament(@RequestBody GetBattleRequest idTournament) {
        log.info("[API REQUEST] Get battles of tournament request with id: {}", idTournament.getBattleId());
        return ResponseEntity.ok(battleService.getBattleOfTournament(idTournament.getBattleId()));
    }
}
