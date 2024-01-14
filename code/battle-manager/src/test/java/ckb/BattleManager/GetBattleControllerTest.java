package ckb.BattleManager;

import ckb.BattleManager.controller.GetBattleController;
import ckb.BattleManager.dto.input.IdLong;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.repository.BattleRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetBattleControllerTest {
    @Autowired
    private GetBattleController getBattleController;

    @Autowired
    private BattleRepository battleRepository;

    @BeforeAll
    public void setUp() {
        Battle battle1 = new Battle();
        battle1.setTournamentId(1L);
        battle1.setRepositoryLink("link1");
        battle1.setMinStudents(1);
        battle1.setMaxStudents(1);
        battle1.setRegDeadline(LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0));
        battle1.setSubDeadline(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0));
        battle1.setBattleToEval(true);
        battleRepository.save(battle1);

        Battle battle2 = new Battle();
        battle2.setTournamentId(1L);
        battle2.setRepositoryLink("link2");
        battle2.setMinStudents(1);
        battle2.setMaxStudents(2);
        battle2.setRegDeadline(LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0));
        battle2.setSubDeadline(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0));
        battle2.setBattleToEval(true);
        battleRepository.save(battle2);

        Battle battle3 = new Battle();
        battle3.setTournamentId(2L);
        battle3.setRepositoryLink("link3");
        battle3.setMinStudents(1);
        battle3.setMaxStudents(2);
        battle3.setRegDeadline(LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0));
        battle3.setSubDeadline(LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0));
        battle3.setBattleToEval(true);
        battleRepository.save(battle3);
    }

    @Test
    public void getBattle() {
        ResponseEntity<Battle> battle = getBattleController.getBattle(new IdLong(1L));

        if (battleRepository.existsById(1L)) {
            assertTrue(battle.getStatusCode().is2xxSuccessful());
            assertNotNull(battle.getBody());
            assertEquals(1L, (long) battle.getBody().getBattleId());
        } else {
            assertTrue(battle.getStatusCode().is4xxClientError());
        }
    }

    @Test
    public void getBattlesOfTournament() {
        ResponseEntity<List<Long>> result = getBattleController.getBattlesOfTournament(new IdLong(1L));

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
    }

    @AfterAll
    public void tearDown() {
        battleRepository.deleteAll();
    }
}
