package ckb.BattleManager;

import ckb.BattleManager.controller.BattleController;
import ckb.BattleManager.dto.IdLong;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.repository.BattleRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BattleControllerTests {
    @Autowired
    private static BattleController battleController;

    @Autowired
    private static BattleRepository battleRepository;

    @BeforeAll
    public static void init() {
        battleRepository.save(new Battle(1L,
                1L,
                "link1",
                1,
                1,
                LocalDateTime.of(2024, Month.JANUARY, 1, 10, 0),
                LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0),
                true)
        );

        battleRepository.save(new Battle(2L,
                1L,
                "link2",
                1,
                2,
                LocalDateTime.of(2024, Month.JANUARY, 2, 10, 0),
                LocalDateTime.of(2024, Month.JANUARY, 10, 12, 0),
                true)
        );

        battleRepository.save(new Battle(3L,
                2L,
                "link3",
                1,
                1,
                LocalDateTime.of(2024, Month.JANUARY, 27, 10, 0),
                LocalDateTime.of(2024, Month.FEBRUARY, 10, 12, 0),
                true)
        );
    }

    @Test
    public void getBattle() {
        ResponseEntity<Battle> battle = battleController.getBattle(new IdLong(1L));

        assertTrue(battle.getStatusCode().is2xxSuccessful());
        assertTrue(battle.getBody().getBattleId() == 1L);


        ResponseEntity<Battle> battleFalse = battleController.getBattle(new IdLong(10L));
        assertFalse(battleFalse.getStatusCode().is4xxClientError());
    }
}
