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
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            User user1 = new User();
            user1.setFullName("Catta");
            user1.setEmail("catta@mail.com");
            user1.setPassword("password");
            user1.setRole(Role.EDUCATOR);
            userRepository.save(user1);
        } catch (DataIntegrityViolationException ignored) {
        }
        try {
            User user2 = new User();
            user2.setFullName("Tommy");
            user2.setEmail("tommy@mail.com");
            user2.setPassword("password");
            user2.setRole(Role.EDUCATOR);
            userRepository.save(user2);
        } catch (DataIntegrityViolationException ignored) {
        }
        try {
            User user3 = new User();
            user3.setFullName("Manu");
            user3.setEmail("manu@mail.com");
            user3.setPassword("password");
            user3.setRole(Role.EDUCATOR);
            userRepository.save(user3);
        } catch (DataIntegrityViolationException ignored) {
        }

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
