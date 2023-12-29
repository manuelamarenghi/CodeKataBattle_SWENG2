package ckb.AccountManager.controller;

import ckb.AccountManager.dto.SignInRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SignInControllerTest {
    @Autowired
    private SignInController signInController;

    @Test
    public void correctCredentialsTest() {
        SignInRequest request = new SignInRequest("catta@mail.com", "password");
        ResponseEntity<Object> response = signInController.signIn(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void wrongCredentialsTest() {
        SignInRequest request = new SignInRequest("catta@mail.com", "wrong_password");
        ResponseEntity<Object> response = signInController.signIn(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

}
