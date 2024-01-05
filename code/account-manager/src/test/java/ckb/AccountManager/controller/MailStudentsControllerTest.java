package ckb.AccountManager.controller;

import ckb.AccountManager.model.Role;
import ckb.AccountManager.model.User;
import ckb.AccountManager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest

public class MailStudentsControllerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailStudentsController mailStudentsController;

    @Test
    public void validRequest() {
        createMockUser();

        ResponseEntity<Object> response = mailStudentsController.getStudentMails();
        List<String> addresses = convertBodyToList(response);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(addresses.contains("ckb.test.user@mail.ckb"));

        deleteMockUser();
    }

    @Test
    public void notFoundRequest() {

        ResponseEntity<Object> response = mailStudentsController.getStudentMails();
        assertNotNull(response.getBody());

        assertTrue(response.getStatusCode().is4xxClientError());
        assertTrue(response.getBody().toString().contains("No students found"));
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

    private void createMockUser() {
        deleteMockUser();

        User user = new User();
        user.setFullName("Test User");
        user.setEmail("ckb.test.user@mail.ckb");
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        userRepository.save(user);
    }

    public void deleteMockUser() {
        userRepository
                .findUserByEmail("ckb.test.user@mail.ckb")
                .ifPresent(user -> userRepository.delete(user));
    }

}
