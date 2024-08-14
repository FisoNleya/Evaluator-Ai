package com.fiso.nleya.marker.setup;


import com.fiso.nleya.marker.auth.user.User;
import com.fiso.nleya.marker.setup.assessment.Assessment;
import com.fiso.nleya.marker.setup.assessment.AssessmentRequest;
import com.fiso.nleya.marker.setup.assessment.AssessmentService;
import com.fiso.nleya.marker.setup.ms.MarkingScheme;
import com.fiso.nleya.marker.setup.ms.MarkingSchemeService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/set-up")
public class SetUpController {


    private final SetUpService setUpService;
    private final MarkingSchemeService markingSchemeService;
    private final AssessmentService assessmentService;



    @PostMapping(value= "marking-scheme",  consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MarkingScheme> loadMarkingScheme(
            @Parameter(description = "Files supported include : {.st }")
            @RequestPart MultipartFile file,
            Principal principal
    ) {


        return ResponseEntity.ok(setUpService.loadMarkingScheme(file, principal.getName()));
    }

    @GetMapping("marking-scheme")
    public ResponseEntity<List<MarkingScheme>> get(
            Principal principal
    ) {
        return new ResponseEntity<>(markingSchemeService.get(principal.getName()), HttpStatus.OK);
    }


    @PostMapping(value = "assessment",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Assessment> createAssessment(
            @Parameter(example = "MS-WEFIOPW", description = "Marking Scheme Id")
            @RequestParam String msId,
            @RequestParam String name,
            @RequestParam(required = false)
            @Parameter(example = "2024-09-03T10:15+02:00")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
            @RequestParam int durationInMinutes,
            @Parameter(description = "Files supported include : {.csv }")
            @RequestPart MultipartFile file,
            Principal principal
    ) {

        return ResponseEntity.ok(assessmentService
                .create(new AssessmentRequest(msId, name, startDate, durationInMinutes, file), principal.getName()));
    }



}
