package com.fiso.nleya.marker.notifications;


import com.fiso.nleya.marker.auth.otp.OtpEmailDto;
import com.fiso.nleya.marker.setup.assessment.AssInvitationDTO;
import com.fiso.nleya.marker.setup.assessment.Assessee;
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
    private static  final String INVITATION = "Evaluator AI Assessment Invitation";

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

    public void prepareAndSendInvitation(AssInvitationDTO invitation, Assessee assessee) {

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            Map<String, Object> map = new HashMap<>();
            map.put("firstName",assessee.getFirstName());
            map.put("code",invitation.code());
            map.put("assessorFullName",invitation.assessorName());
            map.put("assessmentName",invitation.assessmentName());
            map.put("assessmentDate",invitation.date());
            map.put("year", LocalDate.now().getYear());
            Context context = new Context();
            context.setVariables(map);

            String htmlString = springTemplateEngine.process("assessment-invitation", context);
            helper.setSubject(INVITATION);
            helper.setFrom(username);
            helper.setTo(assessee.getEmail());
            helper.setText(htmlString, true);

            sendEmail(assessee.getEmail(),message);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }


    @Async
    @EventListener
    public void onRegistration(OtpEmailDto otpEmailDto) {
        prepareAndSendOTPEmail(otpEmailDto);
    }




    @Async
    @EventListener
    public void onAssessmentCreation(AssInvitationDTO invitationDTO) {

        invitationDTO.assessees()
                .parallelStream()
                .unordered()
                .forEach(assessee -> prepareAndSendInvitation(invitationDTO, assessee));

    }


}
