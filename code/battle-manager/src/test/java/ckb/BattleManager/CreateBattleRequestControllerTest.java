package ckb.BattleManager;

import ckb.BattleManager.controller.CreateBattleController;
import ckb.BattleManager.dto.input.CreateBattleRequest;
import ckb.BattleManager.repository.BattleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CreateBattleRequestControllerTest {
    @Autowired
    private CreateBattleController createBattleController;
    @Autowired
    private BattleRepository battleRepository;

    @Test
    public void createBattle() {
        long idTournament = 1L;
        int minStudents = 1;
        int maxStudents = 2;
        LocalDateTime regDeadline = LocalDateTime.of(2024, Month.JANUARY, 2, 10, 0);
        LocalDateTime subDeadline = LocalDateTime.of(2024, Month.JANUARY, 10, 12, 0);
        boolean battleToEval = true;

        CreateBattleRequest battle = new CreateBattleRequest();

        battle.setTournamentId(idTournament);
        battle.setAuthorId(1L);
        battle.setMinStudents(minStudents);
        battle.setMaxStudents(maxStudents);
        battle.setRegDeadline(regDeadline);
        battle.setSubDeadline(subDeadline);
        battle.setBattleToEval(battleToEval);

        ResponseEntity<Object> retrievedBattle = createBattleController.createBattle(battle);

        assertTrue(retrievedBattle.getStatusCode().is2xxSuccessful());
        //ResponseEntity<Battle> retrieveBattle = getBattleController.getBattlesOfTournament(new IdLong(1L));
        battleRepository.deleteAll();
    }
}