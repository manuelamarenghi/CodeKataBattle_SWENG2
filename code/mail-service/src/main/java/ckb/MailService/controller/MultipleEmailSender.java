package ckb.MailService.controller;

import ckb.MailService.dto.MultipleMailRequest;
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
@RequestMapping("/api/mail-multiple")
@Slf4j
public class MultipleEmailSender {

    @Autowired
    private final JavaMailSender mailSender;
    @Autowired
    private final WebClient webClient;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean sendEmail(@RequestBody MultipleMailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();

        String userIDs = request.getUserIDs();
        String addressesString;
        try {
            addressesString = getEmailAddresses(userIDs);
        } catch (Exception e) {
            log.error("Error while retrieving email address for users {}\n", userIDs);
            return false;
        }

        // need to identify a mail for the "to" field and an array of mails for the "bcc" field
        if (moreThanOneValidMail(addressesString)) {
            int firstComma = addressesString.indexOf(",");
            // first address
            String mailTo = addressesString.substring(0, firstComma);
            // all other addresses
            String[] bcc = addressesString.substring(firstComma+2).split(",");

            message.setTo(mailTo);
            message.setBcc(bcc);
        } else if (noValidMail(addressesString)) {
            log.error("No email was sent due to no valid addresses found in: {}\n", request.getUserIDs());
            log.error("maybe no valid userID was provided?\n");
            return false;
        } else { // only one valid mail was retrieved
            log.warn("Only one valid address found in: {}\n", request.getUserIDs());
            log.warn("Consider sending requests to /api/mail/single\n");
            message.setTo(addressesString);
        }

        message.setSubject("CKB - Notification");
        message.setText(request.getContent());

        try{
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

    private String getEmailAddresses(String userIDs) {
        // request will be constructed like this: http://localhost:8080/api/mail/single?userID=1&userID=2&userID=3 ...
        return webClient.get()
                .uri("http://localhost:8080/api/account/mail",
                        uriBuilder -> uriBuilder.queryParam("userID", userIDs).build())
                .retrieve()
                .bodyToMono(String.class) // we expect the response to only be a String containing the email addresses
                .block(); // block until the response is received
    }
}
