package ckb.MailService.controller;

import ckb.MailService.dto.SingleMailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail/single")
@Slf4j
public class SingleEmailSender {
    @Autowired
    private final JavaMailSender mailSender;
    @Autowired
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean sendEmail(@RequestBody SingleMailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();

        // request will be constructed like this: http://localhost:8080/api/mail/single?userID=1&userID=2&userID=3 ...
        String mail;
        try {
            mail = webClient.get()
                    .uri("http://localhost:8080/api/account/mail",
                            uriBuilder -> uriBuilder.queryParam("userID", request.getUserID()).build())
                    .retrieve()
                    .bodyToMono(String.class) // we expect the response to only be a String containing the email address
                    .block(); // block until the response is received
        } catch (Exception e) {
            log.error("Error while retrieving email address for user {}\n", request.getUserID());
            return false;
        }

        message.setTo(mail);
        message.setSubject(request.getSubject());
        message.setText(request.getContent());

        mailSender.send(message);
        log.info("Email sent to {}\n", mail);
        return true;
    }
}
