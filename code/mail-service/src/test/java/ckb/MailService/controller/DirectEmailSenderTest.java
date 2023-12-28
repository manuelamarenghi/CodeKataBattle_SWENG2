package ckb.MailService.controller;

import ckb.MailService.dto.MultipleMailRequest;
import ckb.MailService.service.MailService;
import org.json.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class DirectEmailSenderTest {
    private static DirectEmailSender directEmailSender;
    private static ClientAndServer mockServer;

    @BeforeAll
    public static void setUp() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("polimi.ckb@gmail.com");
        mailSender.setPassword("soitdnqbieeqoagj");


        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        directEmailSender = new DirectEmailSender(WebClient.create(), new MailService(mailSender));
    }

    @BeforeEach
    public void setUpServer() {
        mockServer = ClientAndServer.startClientAndServer(8080);
    }


    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
    }

    @Test
    public void singleMailRequestTest() {

        mockServer
                .when(request().withMethod("GET").withPath("/api/account/mail")
                        .withQueryStringParameter("userID", "userID1"))
                .respond(response().withStatusCode(200).withBody("luca.cattani@mail.polimi.it"));

        List<String> list = new ArrayList<>();
        list.add("userID1");
        MultipleMailRequest request = new MultipleMailRequest(list, "content");

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
                .when(request().withMethod("GET").withPath("/api/account/mail")
                        .withQueryStringParameter("userID", "userID1", "userID2", "userID3"))
                .respond(response().withStatusCode(200).withBody(answer));

        List<String> list = new ArrayList<>();
        list.add("userID1");
        list.add("userID2");
        list.add("userID3");
        MultipleMailRequest request = new MultipleMailRequest(list, "content");

        ResponseEntity<Object> response = directEmailSender.sendEmail(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void wrongMailRequestTest() {

        mockServer
                .when(request().withMethod("GET").withPath("/api/account/mail")
                        .withQueryStringParameter("userID", "userID1"))
                .respond(response().withStatusCode(200).withBody("luca.cattani@mail.polimi.it"));

        List<String> list = new ArrayList<>();
        list.add("userID4");
        MultipleMailRequest request = new MultipleMailRequest(list, "content");

        ResponseEntity<Object> response = directEmailSender.sendEmail(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }
}
