package com.fiso.nleya.marker.setup;


import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/set-up")
public class SetUpController {


    private final SetUpService setUpService;

    @PostMapping("marking-scheme")
    public MarkingScheme loadMarkingScheme(
            @RequestParam(required = true, defaultValue = "zimsec-history-2019-ms.st")
            @Parameter(example = "zimsec-history-2019-ms.st") String fileName
    ) {

        return setUpService.loadMarkingScheme(fileName);
    }


}
