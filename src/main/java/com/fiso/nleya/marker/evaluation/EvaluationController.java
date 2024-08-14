package com.fiso.nleya.marker.evaluation;


import com.fiso.nleya.marker.evaluation.results.Results;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/assessment/{code}/email/{email}")
    public ResponseEntity<Results> evaluate(
            @PathVariable(name = "code")
            @Parameter(example = "ASS00001", description = "The Assessment code ") String code,
            @PathVariable(name = "email")
            @Parameter(example = "walker@gmail.com") String email,
            @RequestBody @Valid Answers request
    ) {
        return new ResponseEntity<>(evaluationService.evaluate(request, email, code), HttpStatus.OK);
    }

}
