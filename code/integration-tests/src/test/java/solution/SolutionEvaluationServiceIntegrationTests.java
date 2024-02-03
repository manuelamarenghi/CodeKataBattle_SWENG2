package solution;

import ckb.ContainerHandler;
import ckb.dto.account.Role;
import ckb.dto.account.SignUpRequest;
import ckb.dto.battle.*;
import ckb.dto.solution.EvaluationRequest;
import ckb.dto.tournament.NewTournamentRequest;
import ckb.dto.tournament.SubscriptionRequest;
import ckb.model.Battle;
import ckb.model.Tournament;
import ckb.model.WorkingPair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SolutionEvaluationServiceIntegrationTests {
    private final WebClient webClient = WebClient.create();
    private final String solutionEvaluationServiceUrl = "http://localhost:8081/";
    private final String accountManagerUrl = "http://localhost:8086/";
    private final String battleManagerUrl = "http://localhost:8082/";
    private final String tournamentManagerUrl = "http://localhost:8087/";

    private final int STRING_LENGTH = 20;

    @Test
    public void solutionEvaluationTest() throws InterruptedException {
        // create a tournament
        ResponseEntity<Long> educatorCreationResponse = webClient.post()
                .uri(accountManagerUrl + "/api/account/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(SignUpRequest.builder()
                        .email("luca.cattani@mail.polimi.com")
                        .role(Role.EDUCATOR)
                        .fullName("Myself")
                        .password("password")
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Long.class)
                .block();
        System.out.println("Educator created");

        if (educatorCreationResponse != null) {
            assertTrue(educatorCreationResponse.getStatusCode().is2xxSuccessful());
        } else fail();
        Long educatorID = educatorCreationResponse.getBody();


        ResponseEntity<Tournament> tournamentCreationResponse = webClient.post()
                .uri(tournamentManagerUrl + "/api/tournament/new-tournament")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(NewTournamentRequest.builder()
                        .name("MyTournament")
                        .creatorID(educatorID)
                        .regdeadline(new Date(3000 - 1900, Calendar.FEBRUARY, 1))
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Tournament.class)
                .block();

        System.out.println("Tournament created");

        if (tournamentCreationResponse != null) {
            assertTrue(tournamentCreationResponse.getStatusCode().is2xxSuccessful());
        } else fail();
        Tournament tournament = tournamentCreationResponse.getBody();
        assertNotNull(tournament);

        // create a battle with a test official repository
        ResponseEntity<Battle> battleCreationResponse = webClient.post()
                .uri(battleManagerUrl + "/api/battle/create-battle-list")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateBattleRequest(
                        tournament.getTournamentID(),
                        getRandomString(), // battle name
                        educatorID,
                        1, 2, false,
                        LocalDateTime.now().plusSeconds(30),
                        LocalDateTime.now().plusHours(1),
                        List.of(
                                new WorkingPair<>("tests/input_1.txt", "5\n" + "8dafs\n" + "5sjaH\n" + "KS06l\n" + "si83H\n" + "laj74\n" + "-s9k0\n" + "sm_ks\n" + "okauE\n" + "+nuova_partita\n" + "5sjaH\n" + "1\n" + "+stampa_filtrate\n" + "+inserisci_inizio\n" + "AAAAA\n" + "BBBBB\n" + "CCCCC\n" + "+inserisci_fine\n" + "+stampa_filtrate\n" + "5sjaH\n" + "+inserisci_inizio\n" + "DDDDD\n" + "EEEEE\n" + "FFFFF\n" + "+inserisci_fine\n" + "+nuova_partita\n" + "EEEEE\n" + "1\n" + "+stampa_filtrate\n" + "EEEEE"),
                                new WorkingPair<>("tests/output_1.txt", "-s9k0\n" + "5sjaH\n" + "8dafs\n" + "KS06l\n" + "laj74\n" + "okauE\n" + "si83H\n" + "sm_ks\n" + "-s9k0\n" + "5sjaH\n" + "8dafs\n" + "AAAAA\n" + "BBBBB\n" + "CCCCC\n" + "KS06l\n" + "laj74\n" + "okauE\n" + "si83H\n" + "sm_ks\n" + "ok\n" + "-s9k0\n" + "5sjaH\n" + "8dafs\n" + "AAAAA\n" + "BBBBB\n" + "CCCCC\n" + "DDDDD\n" + "EEEEE\n" + "FFFFF\n" + "KS06l\n" + "laj74\n" + "okauE\n" + "si83H\n" + "sm_ks\n" + "ok\n")
                        )
                ))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Battle.class)
                .block();

        System.out.println("Battle created");

        if (battleCreationResponse != null) {
            assertTrue(battleCreationResponse.getStatusCode().is2xxSuccessful());
        } else fail();
        Battle battle = battleCreationResponse.getBody();
        assertNotNull(battle);

        // create a fake team to subscribe to the battle
        ResponseEntity<String> userCreationResponse = webClient.post()
                .uri(accountManagerUrl + "/api/account/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(SignUpRequest.builder()
                        .email("lucacattani2001@gmail.com")
                        .role(Role.STUDENT)
                        .fullName("MyStudentSelf")
                        .password("password")
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block();

        System.out.println("Student created");

        if (userCreationResponse != null) {
            assertTrue(userCreationResponse.getStatusCode().is2xxSuccessful());
        } else fail();
        assertNotNull(userCreationResponse.getBody());
        Long userID = Long.valueOf(userCreationResponse.getBody());


        ResponseEntity<String> tournamentSubcriptionResponse = webClient.post()
                .uri(tournamentManagerUrl + "/api/tournament/subscription")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SubscriptionRequest(tournament.getTournamentID(), userID))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block();

        System.out.println("Subscribed to tournament");

        if (tournamentSubcriptionResponse != null) {
            assertTrue(tournamentSubcriptionResponse.getStatusCode().is2xxSuccessful());
        } else fail();

        ResponseEntity<Object> battleSubcriptionResponse = webClient.post()
                .uri(battleManagerUrl + "/api/battle/join-battle")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JoinRequest.builder()
                        .idStudent(userID)
                        .idBattle(battle.getBattleId())
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Object.class)
                .block();


        System.out.println("Subscribed to battle");

        if (battleSubcriptionResponse != null) {
            assertTrue(battleSubcriptionResponse.getStatusCode().is2xxSuccessful());
        } else fail();

        Thread.sleep(30000); // wait for the battle registration phase to end

        System.out.println("Battle registration phase ended");

        // send evaluation request
        ResponseEntity<TeamInfoMessage> getTeamResponse = webClient.post()
                .uri(battleManagerUrl + "/api/battle/get-team")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(GetTeamStudentRequest.builder()
                        .battleId(battle.getBattleId())
                        .studentId(userID)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(TeamInfoMessage.class)
                .block();

        System.out.println("Got team");

        if (getTeamResponse != null) {
            assertTrue(getTeamResponse.getStatusCode().is2xxSuccessful());
        } else fail();
        assertNotNull(getTeamResponse.getBody());
        Long teamID = getTeamResponse.getBody().getTeamId();


        ResponseEntity<String> evaluationResponse = webClient.post()
                .uri(solutionEvaluationServiceUrl + "/api/solution-evaluation/c")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(EvaluationRequest.builder()
                        .repoUrl("https://github.com/SigCatta/WordChecker.git")
                        .teamId(teamID)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block();

        System.out.println("Sent evaluation request");

        if (evaluationResponse != null) {
            assertTrue(evaluationResponse.getStatusCode().is2xxSuccessful());
        } else fail();
        System.out.println(evaluationResponse.getBody());
        
        // check if the score has been updated

    }

    private String getRandomString() {
        return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                .limit(STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
