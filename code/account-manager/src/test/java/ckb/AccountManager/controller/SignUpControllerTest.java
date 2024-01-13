package ckb.AccountManager.controller;

import ckb.AccountManager.dto.SignUpRequest;
import ckb.AccountManager.model.Role;
import ckb.AccountManager.model.User;
import ckb.AccountManager.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignUpControllerTest {
    @Autowired
    private SignUpController signUpController;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @AfterAll
    public void deleteTestUser() {
        userRepository
                .findUserByEmail("ckb.test.user@mail.ckb")
                .ifPresent(user -> userRepository.delete(user));
    }

    @Test
    public void correctTest() {
        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());

        User user = userRepository.findUserByEmail("ckb.test.user@mail.ckb").orElse(null);

        assertNotNull(user);
    }

    @Test
    public void emailInUseTest() {
        User user = User.builder()
                .fullName("Test User")
                .email("ckb.test.user@mail.ckb")
                .password("password")
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);

        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingEmailTest() {
        SignUpRequest request = new SignUpRequest("", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void invalidEmailFormatTest() {
        SignUpRequest request = new SignUpRequest("SoMe@#Weird_-'1Wrong@Email.idk....", "Test User", "password", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }


    @Test
    public void missingNameTest() {
        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "", "password", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void missingPasswordTest() {
        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "Test User", "", Role.STUDENT);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void invalidRoleTest() {
        SignUpRequest request = new SignUpRequest("ckb.test.user@mail.ckb", "Test User", "password", null);
        ResponseEntity<Object> response = signUpController.signUp(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

}
