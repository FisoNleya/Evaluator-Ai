package com.fiso.nleya.marker.evaluation.results;

public record Result(
        String question,
        String studentAnswer,
        String expectedAnswer,
        String evaluation,
        String explanation
){

}
