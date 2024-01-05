package ckb.MailService.controller;

import ckb.MailService.dto.DirectMailRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AllStudentsEmailSenderIT {
    @Autowired
    private DirectEmailSender directEmailSender;

    @Test
    public void singleStudentTest() {

        DirectMailRequest request = new DirectMailRequest(List.of("1"), "content");

        directEmailSender.sendEmail(request);
    }
}