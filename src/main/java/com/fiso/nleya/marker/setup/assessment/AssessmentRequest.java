package com.fiso.nleya.marker.setup.assessment;

import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;

public record AssessmentRequest(
         String msId,
         String assessmentName,
         ZonedDateTime startDate,
         int durationInMinutes,
         MultipartFile file

) {
}
