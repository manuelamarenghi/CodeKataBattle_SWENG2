package ckb.MailService.controller;

import ckb.MailService.dto.AllStudentsMailRequest;
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

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class AllStudentsEmailSenderTest {
    private static AllStudentsEmailSender allStudentsEmailSender;
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

        allStudentsEmailSender = new AllStudentsEmailSender(new MailService(mailSender), WebClient.builder().build() );
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
    public void singleStudentTest() {
        mockServer
                .when(request().withMethod("GET").withPath("/api/account/mail-students"))
                .respond(response().withStatusCode(200).withBody("luca.cattani@mail.polimi.it"));

        AllStudentsMailRequest request = new AllStudentsMailRequest("content");

        ResponseEntity<Object> response = allStudentsEmailSender.sendEmail(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void multipleStudentsTest() {


        JSONArray jsonArray = new JSONArray();
        jsonArray.put("luca.cattani@mail.polimi.it");
        jsonArray.put("lucacattani2001@gmail.com");

        String answer = jsonArray.toString().substring(1, jsonArray.toString().length() - 1);

        mockServer
                .when(request().withMethod("GET").withPath("/api/account/mail-students"))
                .respond(response().withStatusCode(200).withBody(answer));

        AllStudentsMailRequest request = new AllStudentsMailRequest("content");

        ResponseEntity<Object> response = allStudentsEmailSender.sendEmail(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void noStudentsTest() {

        mockServer
                .when(request().withMethod("GET").withPath("/api/account/mail-students"))
                .respond(response().withStatusCode(404));

        AllStudentsMailRequest request = new AllStudentsMailRequest("content");

        ResponseEntity<Object> response = allStudentsEmailSender.sendEmail(request);

        assertTrue(response.getStatusCode().is4xxClientError());
    }
}
