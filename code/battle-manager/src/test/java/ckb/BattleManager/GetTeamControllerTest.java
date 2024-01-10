package ckb.BattleManager;

import ckb.BattleManager.controller.GetTeamController;
import ckb.BattleManager.repository.TeamRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GetTeamControllerTest {
    private final GetTeamController getTeamController;
    private final TeamRepository teamRepository;

    @Autowired
    public GetTeamControllerTest(GetTeamController getTeamController, TeamRepository teamRepository) {
        this.getTeamController = getTeamController;
        this.teamRepository = teamRepository;
    }

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        teamRepository.deleteAll();
    }
}