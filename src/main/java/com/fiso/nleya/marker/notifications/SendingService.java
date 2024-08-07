package com.fiso.nleya.marker.notifications;


import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
public class SendingService {


    @Value("${spring.mail.username}")
    public String username;

    private final JavaMailSender mailSender;


    public SendingService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }



    public void sendEmail(String to, MimeMessage message) {
        try {
            mailSender.send(message);
            log.info(" Message sent :: Sender : >" + username + " Sending to : " + to);
        } catch (MailException e) {
            log.error("Message sending failed ".concat(e.getMessage()));
        }

    }


}
