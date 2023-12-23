package ckb.MailService.controller;

import ckb.MailService.dto.MultipleMailRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MultipleEmailSenderTest {
    private static MultipleEmailSender multipleEmailSender;
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

        multipleEmailSender = new MultipleEmailSender(mailSender, WebClient.builder().build());
        mockServer = ClientAndServer.startClientAndServer(8080);

    }

    @AfterAll
    public static void tearDown() {
        mockServer.stop();
    }

    @Test
    public void singleMailRequestTest() {

        mockServer
                .when(request().withMethod("GET").withPath("/api/account/mail")
                        .withQueryStringParameter("userID", "userID1"))
                .respond(response().withStatusCode(200).withBody("luca.cattani@mail.polimi.it"));

        MultipleMailRequest request = new MultipleMailRequest("userID1", "subject", "content");

        boolean success = multipleEmailSender.sendEmail(request);

        assertTrue(success);
    }

    @Test
    public void multipleMailRequestTest() {

        mockServer
                .when(request().withMethod("GET").withPath("/api/account/mail")
                        .withQueryStringParameter("userID", "userID1, userID2, userID3"))
                .respond(response().withStatusCode(200).withBody("luca.cattani@mail.polimi.it, lucacattani2001@gmail.com, lucacattani.job@gmail.com"));

        MultipleMailRequest request = new MultipleMailRequest("userID1, userID2, userID3", "subject", "content");

        boolean success = multipleEmailSender.sendEmail(request);

        assertTrue(success);
    }

    @Test
    public void wrongMailRequestTest() {

        mockServer
                .when(request().withMethod("GET").withPath("/api/account/mail")
                        .withQueryStringParameter("userID", "userID1"))
                .respond(response().withStatusCode(200).withBody("luca.cattani@mail.polimi.it"));

        MultipleMailRequest request = new MultipleMailRequest("userID4", "subject", "content");

        boolean success = multipleEmailSender.sendEmail(request);

        assertFalse(success);
    }
}
