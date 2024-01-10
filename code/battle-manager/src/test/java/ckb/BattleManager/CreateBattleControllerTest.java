package ckb.BattleManager;

import ckb.BattleManager.controller.CreateBattleController;
import ckb.BattleManager.controller.GetBattleController;
import ckb.BattleManager.model.Battle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CreateBattleControllerTest {
    @Autowired
    private CreateBattleController createBattleController;
    @Autowired
    private GetBattleController getBattleController;

    @Test
    public void createBattle() {
        long idTournament = 1L;
        String repositoryLink = "link4";
        int minStudents = 1;
        int maxStudents = 2;
        LocalDateTime regDeadline = LocalDateTime.of(2024, Month.JANUARY, 2, 10, 0);
        LocalDateTime subDeadline = LocalDateTime.of(2024, Month.JANUARY, 10, 12, 0);
        boolean battleToEval = true;

        Battle battle = new Battle();

        battle.setTournamentId(idTournament);
        battle.setRepositoryLink(repositoryLink);
        battle.setMinStudents(minStudents);
        battle.setMaxStudents(maxStudents);
        battle.setRegDeadline(regDeadline);
        battle.setSubDeadline(subDeadline);
        battle.setBattleToEval(battleToEval);

        ResponseEntity<Object> retrievedBattle = createBattleController.createBattle(battle);

        assertTrue(retrievedBattle.getStatusCode().is2xxSuccessful());
        //ResponseEntity<Battle> retrieveBattle = getBattleController.getBattlesOfTournament(new IdLong(1L));
    }
}