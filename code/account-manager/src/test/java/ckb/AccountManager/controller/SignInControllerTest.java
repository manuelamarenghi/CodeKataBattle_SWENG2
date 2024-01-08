package ckb.AccountManager.controller;

import ckb.AccountManager.dto.SignInRequest;
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

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignInControllerTest {
    @Autowired
    private SignInController signInController;
    @Autowired
    private UserRepository userRepository;

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
    }

    @Test
    public void correctCredentialsTest() {
        SignInRequest request = new SignInRequest("catta@mail.com", "password");
        ResponseEntity<Object> response = signInController.signIn(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void wrongCredentialsTest() {
        SignInRequest request = new SignInRequest("somerandomemail@mail.com", "wrong_password");
        ResponseEntity<Object> response = signInController.signIn(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

}
