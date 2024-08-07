package com.fiso.nleya.marker.notifications;


import com.fiso.nleya.marker.auth.otp.OtpEmailDto;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserMailingService extends SendingService {

    private final SpringTemplateEngine springTemplateEngine;
    private final JavaMailSender mailSender;

    private static  final String REGISTRATION = "Evaluator AI Registration";

    public UserMailingService(SpringTemplateEngine springTemplateEngine, JavaMailSender mailSender) {
        super(mailSender);
        this.springTemplateEngine = springTemplateEngine;
        this.mailSender = mailSender;
    }


    public void prepareAndSendOTPEmail(OtpEmailDto otpEmailDto) {

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Map<String, Object> map = new HashMap<>();
            map.put("firstName",otpEmailDto.firstName());
            map.put("code",otpEmailDto.otpCode());
            map.put("year", LocalDate.now().getYear());
            Context context = new Context();
            context.setVariables(map);

            String htmlString = springTemplateEngine.process("account-verification", context);
            helper.setSubject(REGISTRATION);
            helper.setFrom(username);
            helper.setTo(otpEmailDto.email());
            helper.setText(htmlString, true);

            sendEmail(otpEmailDto.email(),message);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    @Async
    @EventListener
    public void onRegistration(OtpEmailDto otpEmailDto) {
        prepareAndSendOTPEmail(otpEmailDto);
    }



}
