package ckb.MailService.controller;

import ckb.MailService.dto.DirectMailRequest;
import org.json.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
public class DirectEmailSenderUT {
    @Autowired
    private DirectEmailSender directEmailSender;
    private ClientAndServer mockServer;

    @BeforeEach
    public void setUpServer() {
        mockServer = ClientAndServer.startClientAndServer(8086);
    }


    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }

    @Test
    public void singleMailRequestTest() {

        mockServer
                .when(request().withMethod("POST").withPath("/api/account/mail"))
                .respond(response().withStatusCode(200).withBody("luca.cattani@mail.polimi.it"));

        List<String> list = new ArrayList<>();
        list.add("userID1");
        DirectMailRequest request = new DirectMailRequest(list, "content");

        ResponseEntity<Object> response = directEmailSender.sendEmail(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void multipleMailRequestTest() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("luca.cattani@mail.polimi.it");
        jsonArray.put("lucacattani2001@gmail.com");
        jsonArray.put("lucacattani.job@gmail.com");

        String answer = jsonArray.toString().substring(1, jsonArray.toString().length() - 1);

        mockServer
                .when(request().withMethod("POST").withPath("/api/account/mail"))
                .respond(response().withStatusCode(200).withBody(answer));

        List<String> list = new ArrayList<>();
        list.add("userID1");
        list.add("userID2");
        list.add("userID3");
        DirectMailRequest request = new DirectMailRequest(list, "content");

        ResponseEntity<Object> response = directEmailSender.sendEmail(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void wrongMailRequestTest() {

        mockServer
                .when(request().withMethod("POST").withPath("/api/account/mail"))
                .respond(response().withStatusCode(404));

        List<String> list = new ArrayList<>();
        list.add("userID4");
        DirectMailRequest request = new DirectMailRequest(list, "content");

        ResponseEntity<Object> response = directEmailSender.sendEmail(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }
}
