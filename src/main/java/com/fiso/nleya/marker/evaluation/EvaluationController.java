package com.fiso.nleya.marker.evaluation;


import com.fiso.nleya.marker.evaluation.results.Results;
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

    @PostMapping
    public ResponseEntity<Results> evaluate(
            @RequestBody @Valid Answers request
    ) {
        return new ResponseEntity<>(evaluationService.evaluate(request), HttpStatus.OK);
    }

}
