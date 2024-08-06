package com.fiso.nleya.marker.shared.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> dataNotFoundExceptionHandler(DataNotFoundException ex) {
        ErrorMessage customError = ErrorMessage.builder()
                .eventTime(ZonedDateTime.now())
                .errorDescription(ex.getLocalizedMessage())
                .errorCode(HttpStatus.NOT_FOUND)
                .build();
        log.error(customError.toString(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(customError);
    }


    @ExceptionHandler(DuplicateRecordException.class)
    public ResponseEntity<Object> duplicateRecordExceptionHandler(DuplicateRecordException ex) {
        ErrorMessage customError = ErrorMessage.builder()
                .eventTime(ZonedDateTime.now())
                .errorDescription(ex.getLocalizedMessage())
                .errorCode(HttpStatus.CONFLICT)
                .build();
        log.error(customError.toString(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(customError);
    }


    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> invalidRequestHandler(InvalidRequestException ex) {
        ErrorMessage customError = ErrorMessage.builder()
                .eventTime(ZonedDateTime.now())
                .errorDescription(ex.getLocalizedMessage())
                .errorCode(HttpStatus.BAD_REQUEST)
                .build();
        log.error(customError.toString(), ex);
        return ResponseEntity.badRequest().body(customError);
    }


    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Object> jsonProcessingExceptionHandler(JsonProcessingException ex) {
        ErrorMessage customError = ErrorMessage.builder()
                .eventTime(ZonedDateTime.now())
                .errorDescription(ex.getLocalizedMessage())
                .errorCode(HttpStatus.EXPECTATION_FAILED)
                .build();
        log.error(customError.toString(), ex);
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(customError);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> generalSqlExceptionHandler(SQLException ex) {
        ErrorMessage customError = ErrorMessage.builder()
                .eventTime(ZonedDateTime.now())
                .errorDescription(ex.getLocalizedMessage())
                .errorCode(HttpStatus.EXPECTATION_FAILED)
                .build();
        log.error(customError.toString(), ex);
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(customError);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> nullPointerExceptionHandler(NullPointerException ex) {
        ErrorMessage customError = ErrorMessage.builder()
                .eventTime(ZonedDateTime.now())
                .errorDescription(ex.getLocalizedMessage())
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        log.error(customError.toString(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> generalExceptionHandler(Exception ex) {
        ErrorMessage customError = ErrorMessage.builder()
                .eventTime(ZonedDateTime.now())
                .errorDescription(ex.getLocalizedMessage())
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        log.error(customError.toString(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customError);
    }



    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {

        ErrorMessage customError = ErrorMessage.builder()
                .eventTime(ZonedDateTime.now())
                .errorDescription("")
                .errorCode(HttpStatus.BAD_REQUEST)
                .build();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            customError.setErrorDescription(customError.getErrorDescription()+ fieldError.getField()+" : "+
                    fieldError.getDefaultMessage()+".| ");
        }

        return new ResponseEntity<>( customError, HttpStatus.BAD_REQUEST);
    }



}
