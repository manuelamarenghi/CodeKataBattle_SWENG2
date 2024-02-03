package ckb.BattleManager.controller;

import ckb.BattleManager.dto.input.GetTeamStudentRequest;
import ckb.BattleManager.dto.input.GetTeamsRequest;
import ckb.BattleManager.dto.output.TeamInfoMessage;
import ckb.BattleManager.dto.output.TeamsRankingMessage;
import ckb.BattleManager.model.Battle;
import ckb.BattleManager.model.Participation;
import ckb.BattleManager.model.Team;
import ckb.BattleManager.model.WorkingPair;
import ckb.BattleManager.repository.BattleRepository;
import ckb.BattleManager.repository.ParticipationRepository;
import ckb.BattleManager.repository.TeamRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetTeamControllerTest {
    private final GetTeamController getTeamController;
    private final BattleRepository battleRepository;
    private final TeamRepository teamRepository;
    private final ParticipationRepository participationRepository;
    private ClientAndServer mockServerAccountManager;
    private Battle battle;
    private Team team1, team2, team3;

    @Autowired
    public GetTeamControllerTest(GetTeamController getTeamController, TeamRepository teamRepository,
                                 BattleRepository battleRepository, ParticipationRepository participationRepository) {
        this.getTeamController = getTeamController;
        this.teamRepository = teamRepository;
        this.battleRepository = battleRepository;
        this.participationRepository = participationRepository;
    }

    @BeforeAll
    void setUp() throws JSONException {
        getTeamController.initTestMode();
        battle = new Battle();
        battle.setTournamentId(1L);
        battle.setRepositoryLink("link");

        team1 = new Team();
        team1.setBattle(battle);
        team1.setScore(0);
        team1.setEduEvaluated(false);
        team1.setCanParticipateToBattle(true);

        team2 = new Team();
        team2.setBattle(battle);
        team2.setScore(20);
        team2.setEduEvaluated(false);
        team2.setCanParticipateToBattle(true);

        battle.setTeamsRegistered(List.of(team1, team2));
        battleRepository.save(battle);

        Participation participation1 = new Participation();
        participation1.setStudentId(1L);
        participation1.setTeam(team1);

        participationRepository.save(participation1);

        Participation participation2 = new Participation();
        participation2.setStudentId(2L);
        participation2.setTeam(team1);

        participationRepository.save(participation2);

        Participation participation3 = new Participation();
        participation3.setStudentId(3L);
        participation3.setTeam(team2);
        participationRepository.save(participation3);

        team3 = new Team();
        team3.setBattle(battle);
        team3.setScore(20);
        team3.setEduEvaluated(false);
        team3.setCanParticipateToBattle(false);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        jsonObject.put("email", "example@example.com");
        jsonObject.put("fullName", "John Doe");
        jsonObject.put("password", "password123");
        jsonObject.put("role", "STUDENT");
        mockServerAccountManager = ClientAndServer.startClientAndServer(8086);
        mockServerAccountManager
                .when(request().withPath("/api/account/user"))
                .respond(response().withStatusCode(200).withBody(
                        jsonObject.toString()
                ).withContentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getTeam() {
        ResponseEntity<TeamInfoMessage> retrievedTeam = getTeamController.
                getTeam(new GetTeamStudentRequest(battle.getBattleId(), 1L));

        assertTrue(retrievedTeam.getStatusCode().is2xxSuccessful());
        assertNotNull(retrievedTeam.getBody());

        TeamInfoMessage teamInfoMessage = retrievedTeam.getBody();
        System.out.println(teamInfoMessage);
        assertEquals(team1.getTeamId(), teamInfoMessage.getTeamId());
        assertEquals(teamInfoMessage.getParticipantsName().size(), 2);
    }

    @Test
    public void getNonExistingTeam() {
        ResponseEntity<TeamInfoMessage> retrievedTeam = getTeamController.
                getTeam(new GetTeamStudentRequest(battle.getBattleId(), 0L));

        assertTrue(retrievedTeam.getStatusCode().is4xxClientError());
    }

    @Test
    public void getNonExistingBattle() {
        ResponseEntity<TeamInfoMessage> retrievedTeam = getTeamController.
                getTeam(new GetTeamStudentRequest(0L, 1L));

        assertTrue(retrievedTeam.getStatusCode().is4xxClientError());
    }

    @Test
    public void getTeamsOfBattle() {
        ResponseEntity<TeamsRankingMessage> retrievedTeams = getTeamController
                .getTeamsOfBattle(new GetTeamsRequest(battle.getBattleId()));

        assertTrue(retrievedTeams.getStatusCode().is2xxSuccessful());
        assertNotNull(retrievedTeams.getBody());

        TeamsRankingMessage teamsRankingMessage = retrievedTeams.getBody();

        List<WorkingPair<Long, Integer>> listTeamsIdScore = teamsRankingMessage.getListTeamsIdScore();
        assertEquals(2, listTeamsIdScore.size());

        System.out.println(teamsRankingMessage);
        assertTrue(listTeamsIdScore.get(0).getRight() >= listTeamsIdScore.get(1).getRight());
    }

    @AfterAll
    void tearDown() {
        mockServerAccountManager.stop();
        participationRepository.deleteAll();
        teamRepository.deleteAll();
        battleRepository.deleteAll();
    }
}