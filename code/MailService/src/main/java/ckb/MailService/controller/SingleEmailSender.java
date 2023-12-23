package ckb.MailService.controller;

import ckb.MailService.dto.SingleMailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;


@Service
@RestController
@RequestMapping("/api/mail/single")
@Slf4j
public class SingleEmailSender {
    @Autowired
    private JavaMailSender mailSender;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void sendEmail(@RequestBody SingleMailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();

        // String userID = request.getUserID();
        String mail = "luca.cattani@mail.polimi.it"; // only for testing...
        //TODO get email from userID -- request to account manager

        message.setTo(mail);
        message.setSubject(request.getSubject());
        message.setText(request.getContent());

        mailSender.send(message);
        log.info("Email sent to {}\n", mail);
    }
}
