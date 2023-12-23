package ckb.MailService.controller;

import ckb.MailService.dto.MultipleMailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@RestController
@RequestMapping("/api/mail/multiple")
@Slf4j
public class MultipleEmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void sendEmail(@RequestBody MultipleMailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();

        // String userIDs = request.getUserIDs();
        String mails = "luca.cattani@mail.polimi.it, lucacattani.job@gmail.com"; // only for testing...
        //TODO get emails from userIDs -- request to account manager

        // need to identify a mail for the "to" field and a list of mails for the "bcc" field
        int idx = mails.indexOf(',');

        if(moreThenOneMail(mails)){
            message.setTo(mails.substring(0, idx));
            message.setBcc(mails.substring(idx+2));
        } else if (noValidMail(mails)){
            log.warn("No email was sent due to no valid addresses found in: {}\n", mails);
            log.warn("maybe no valid userID was provided?\n");
            return;
        } else{
            log.warn("Only one valid address found in: {}\n", mails);
            log.warn("Consider sending requests to /api/mail/single\n");
            message.setTo(mails);
        }
        message.setSubject(request.getSubject());
        message.setText(request.getContent());

        mailSender.send(message);
        log.info("Email sent to {}\n", mails);
    }

    private boolean moreThenOneMail(String mails) {
        return mails.indexOf(',') != -1;
    }
    private boolean noValidMail(String mails) {
        return mails.indexOf('@') == -1;
    }

}
