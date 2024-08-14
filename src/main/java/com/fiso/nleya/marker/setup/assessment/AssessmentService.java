package com.fiso.nleya.marker.setup.assessment;


import com.fiso.nleya.marker.auth.user.User;
import com.fiso.nleya.marker.auth.user.UserService;
import com.fiso.nleya.marker.setup.ms.MarkingScheme;
import com.fiso.nleya.marker.setup.ms.MarkingSchemeService;
import com.fiso.nleya.marker.shared.exceptions.DataNotFoundException;
import com.fiso.nleya.marker.shared.exceptions.InvalidRequestException;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final UserService userService;
    private final MarkingSchemeService markingSchemeService;
    private final AssessmentRepository assessmentRepository;
    private final AssesseeRepository assesseeRepository;

    private final ApplicationEventPublisher publisher;

    private static final String ASS_PREFIX = "ASS";


    public Assessment create(AssessmentRequest request, String userEmail) {

        User user = userService.findByEmail(userEmail);
        MarkingScheme markingScheme = markingSchemeService.findById(request.msId());

        List<Assessee> assessees = readCSv(request.file());
        assessees = assesseeRepository.saveAll(assessees);

        ZonedDateTime startDate = request.startDate();
        Assessment assessment = assessmentRepository.save(Assessment.builder()
                .markingScheme(markingScheme)
                .assessor(user)
                .name(request.assessmentName())
                .assesees(new HashSet<>(assessees))
                .startDate(startDate)
                .endDate(startDate.plusMinutes(request.durationInMinutes()))
                .durationInMinutes(request.durationInMinutes())
                .build());

        String code = generateCode(assessment.getId());
        assessment.setCode(code);
        assessment = assessmentRepository.save(assessment);

        //send notifications to all assessees
        String accessorFullName = user.getFirstName() + " " + user.getLastName();
        String formattedDate = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).format(startDate);

        publisher.publishEvent(new AssInvitationDTO(assessees, request.assessmentName(), accessorFullName, code, formattedDate));
        return assessment;

    }

    public Assessment findByAssessesAndCode(String email, String code) {
        return assessmentRepository.findByAssesees_EmailAndCode(email, code)
                .orElseThrow(() -> new DataNotFoundException("This email is not register for the requested assessment : " + email));
    }

    String generateCode(long id) {
        return ASS_PREFIX + String.format("%06d", id);
    }


    List<Assessee> readCSv(MultipartFile file) {
        List<Assessee> assessees = new ArrayList<>();
        try {
            assessees = new CsvToBeanBuilder(new InputStreamReader(file.getInputStream()))
                    .withType(Assessee.class).build().parse();
        } catch (IOException e) {
            throw new InvalidRequestException("Failed to extract csv content " + e.getMessage());
        }
        return assessees;
    }

}
