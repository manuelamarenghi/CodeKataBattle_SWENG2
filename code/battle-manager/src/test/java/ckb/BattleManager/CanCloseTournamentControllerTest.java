package ckb.BattleManager;

import ckb.BattleManager.controller.CanCloseTournamentController;
import ckb.BattleManager.dto.input.IdLong;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.repository.BattleRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CanCloseTournamentControllerTest {
    private final CanCloseTournamentController canCloseTournamentController;
    private final BattleRepository battleRepository;

    @Autowired
    public CanCloseTournamentControllerTest(CanCloseTournamentController canCloseTournamentController,
                                            BattleRepository battleRepository) {
        this.canCloseTournamentController = canCloseTournamentController;
        this.battleRepository = battleRepository;
    }

    @BeforeAll
    void setUp() {
        Battle battle = new Battle();
        battle.setTournamentId(1L);
        battle.setRepositoryLink("link1");
        battle.setSubDeadline(LocalDateTime.of(2022, 1, 1, 0, 0));
        battleRepository.save(battle);

        battle = new Battle();
        battle.setTournamentId(1L);
        battle.setRepositoryLink("link2");
        battle.setSubDeadline(LocalDateTime.of(2022, 1, 2, 0, 0));
        battleRepository.save(battle);

        battle = new Battle();
        battle.setTournamentId(2L);
        battle.setRepositoryLink("link3");
        battle.setSubDeadline(LocalDateTime.of(2022, 1, 1, 0, 0));
        battleRepository.save(battle);

        battle = new Battle();
        battle.setTournamentId(2L);
        battle.setRepositoryLink("link4");
        battle.setSubDeadline(LocalDateTime.now().plusDays(1));
        battleRepository.save(battle);
    }

    @Test
    void canCloseTournamentSuccess() {
        assertEquals(Boolean.TRUE, canCloseTournamentController.canCloseTournament(new IdLong(1L)).getBody());
    }

    @Test
    void canCloseTournamentFail() {
        assertEquals(Boolean.FALSE, canCloseTournamentController.canCloseTournament(new IdLong(2L)).getBody());
    }

    @AfterAll
    void tearDown() {
        battleRepository.deleteAll();
    }
}