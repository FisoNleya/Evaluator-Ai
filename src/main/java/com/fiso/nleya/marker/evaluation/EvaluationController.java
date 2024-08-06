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

    @PostMapping("ms/{msId}")
    public ResponseEntity<Results> evaluate(
            @PathVariable(name = "msId")
            @Parameter(example = "MS00001", description = "The question paper/ marking scheme Id") String msId,
            @RequestBody @Valid Answers request
    ) {
        return new ResponseEntity<>(evaluationService.evaluate(request, msId), HttpStatus.OK);
    }

}
