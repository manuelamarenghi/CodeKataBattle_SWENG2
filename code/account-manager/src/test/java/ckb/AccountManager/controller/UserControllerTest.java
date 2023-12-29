package ckb.AccountManager.controller;

import ckb.AccountManager.dto.UserRequest;
import ckb.AccountManager.model.Role;
import ckb.AccountManager.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserControllerTest {
    @Autowired
    UserController userController;

    @Test
    public void userFoundTest() {
        UserRequest request = new UserRequest();
        request.setUserID(1L);
        ResponseEntity<Object> response = userController.getUser(request);
        assert response.getBody() != null;

        User user = (User) response.getBody();

        assertEquals(1L, user.getId());
        assertEquals("catta@mail.com", user.getEmail());
        assertEquals("Catta", user.getFullName());
        assertEquals("password", user.getPassword());
        assertEquals(user.getRole(), Role.EDUCATOR);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void userNotFoundTest() {
        UserRequest request = new UserRequest();
        request.setUserID(0L);
        ResponseEntity<Object> response = userController.getUser(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }
}
