package com.fiso.nleya.marker.setup;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/set-up")
public class SetUpController {


    private final SetUpService setUpService;



    @PostMapping(value= "marking-scheme",  consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<MarkingScheme> loadMarkingScheme(
            @Parameter(description = "Files supported include : {.st }")
            @RequestPart MultipartFile file
    ) {

        return ResponseEntity.ok(setUpService.loadMarkingScheme(file));
    }


}
