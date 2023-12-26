package ckb.MailService.controller;

import ckb.MailService.dto.AllStudentsMailRequest;
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
@RequestMapping("/api/mail-all-students")
@Slf4j
public class AllStudentsEmailSender {


    @Autowired
    private final JavaMailSender mailSender;
    @Autowired
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean sendEmail(@RequestBody AllStudentsMailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();

        String addressesString;
        try {
            addressesString = getEmailAddresses();
        } catch (Exception e) {
            log.error("Error while retrieving email address for students\n");
            return false;
        }

        if (moreThanOneValidMail(addressesString)) {
            int firstComma = addressesString.indexOf(",");
            // first address
            String mailTo = addressesString.substring(0, firstComma);
            // all other addresses
            String[] bcc = addressesString.substring(firstComma + 2).split(",");

            message.setTo(mailTo);
            message.setBcc(bcc);
        } else if (noValidMail(addressesString)) {
            log.error("No email was sent due to no valid student mail address found\n");
            return false;
        } else { // only one valid mail was retrieved
            log.warn("Only one valid address found\n");
            message.setTo(addressesString);
        }

        message.setSubject("CKB - Notification");
        message.setText(request.getContent());

        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error while sending email to {}\n", addressesString);
            return false;
        }
        log.info("Email sent to {}\n", addressesString);
        return true;
    }

    private boolean moreThanOneValidMail(String string) {
        return string.indexOf(',') > 0;
    }

    private boolean noValidMail(String string) {
        return string == null || string.isEmpty() || string.indexOf('@') < 0;
    }

    private String getEmailAddresses() {
        return webClient.get()
                .uri("http://localhost:8080/api/account/mail-students")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
