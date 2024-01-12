package ckb.AccountManager.controller;

import ckb.AccountManager.dto.MailRequest;
import ckb.AccountManager.model.Role;
import ckb.AccountManager.model.User;
import ckb.AccountManager.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MailControllerTest {

    @Autowired
    private MailController mailController;
    @Autowired
    private UserRepository userRepository;

    private List<String> validIDs;

    @BeforeAll
    public void setUp() {
        userRepository.deleteAll();
        User user1 = User.builder()
                .fullName("Catta")
                .email("catta@mail.com")
                .password("password")
                .role(Role.EDUCATOR)
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .fullName("Tommy")
                .email("tommy@mail.com")
                .password("password")
                .role(Role.EDUCATOR)
                .build();
        userRepository.save(user2);

        User user3 = User.builder()
                .fullName("Manu")
                .email("manu@mail.com")
                .password("password")
                .role(Role.EDUCATOR)
                .build();
        userRepository.save(user3);

        validIDs = List.of(
                userRepository.findUserByEmail("catta@mail.com").map(user -> String.valueOf(user.getId())).orElse("0"),
                userRepository.findUserByEmail("tommy@mail.com").map(user -> String.valueOf(user.getId())).orElse("0"),
                userRepository.findUserByEmail("manu@mail.com").map(user -> String.valueOf(user.getId())).orElse("0")
        );
    }

    @Test
    public void validRequest() {
        ResponseEntity<Object> response = mailController.getMail(new MailRequest(validIDs));

        List<String> addresses = convertBodyToList(response);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(addresses.contains("catta@mail.com"));
        assertTrue(addresses.contains("tommy@mail.com"));
        assertTrue(addresses.contains("manu@mail.com"));
    }

    @Test
    public void badRequest() {
        ResponseEntity<Object> response = mailController.getMail(new MailRequest(List.of("4", "5", "6")));

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void partialRequest() {
        List<String> partiallyValidIds = new ArrayList<>(validIDs);
        partiallyValidIds.add("-1");
        ResponseEntity<Object> response = mailController.getMail(new MailRequest(partiallyValidIds));
        // userID -1 should not return any valid address

        List<String> addresses = convertBodyToList(response);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(addresses.contains("catta@mail.com"));
        assertTrue(addresses.contains("tommy@mail.com"));
        assertTrue(addresses.contains("manu@mail.com"));
    }

    private List<String> convertBodyToList(ResponseEntity<Object> response) {
        assertNotNull(response.getBody());
        return Arrays.stream(
                        response.getBody()
                                .toString()
                                .split(",")
                )
                .map(s -> s.replace("[", "").replace("]", "").trim())
                .toList();
    }
}
