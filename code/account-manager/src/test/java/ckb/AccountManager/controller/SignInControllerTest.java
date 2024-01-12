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
