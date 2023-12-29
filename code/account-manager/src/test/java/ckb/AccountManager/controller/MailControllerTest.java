package ckb.AccountManager.controller;

import ckb.AccountManager.dto.MailRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MailControllerTest {

    @Autowired
    private MailController mailController;

    @Test
    public void validRequest() {

        ResponseEntity<Object> response = mailController.getMail(new MailRequest(List.of("1", "2", "3")));
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
        ResponseEntity<Object> response = mailController.getMail(new MailRequest(List.of("1", "2", "3", "-1")));
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
