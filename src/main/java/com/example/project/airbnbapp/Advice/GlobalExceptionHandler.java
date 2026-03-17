package com.example.project.airbnbapp.Advice;

import com.example.project.airbnbapp.Exception.ConflictException;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIError> resourceNotFound(ResourceNotFoundException rnfe) {

        String dateAndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
        //using lombok package private constructor
        APIError apiError = APIError.builder()
                .message(rnfe.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .errorRecordedTime(dateAndTime)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> validationErrors(MethodArgumentNotValidException manve) {

        //getting all the binding errors from the exception and converting it to List<String> using stream
        List<String> errorList = manve.getBindingResult()
                .getAllErrors()
                .stream().map(error->error.getDefaultMessage())
                .collect(Collectors.toList());

        String dateAndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
        //using lombok builder
        APIError apiError = APIError.builder()
                .message("Input validation failed")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .errorRecordedTime(dateAndTime)
                .subErrors(errorList)
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<APIError> conflictError(ConflictException exception) {

        //using lombok builder
        APIError apiError = APIError.builder()
                .message(exception.getMessage())
                .errorRecordedTime(returnCurrentDateTime())
                .httpStatus(HttpStatus.CONFLICT)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<APIError> handleAuthenticationException(AuthenticationException exception) {

        APIError apiError = APIError.builder()
                .message(exception.getMessage())
                .errorRecordedTime(returnCurrentDateTime())
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<APIError> handleJwtException(JwtException exception) {

        APIError apiError = APIError.builder()
                .message(exception.getMessage())
                .errorRecordedTime(returnCurrentDateTime())
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<APIError> handleAccessDeniedException(AccessDeniedException exception) {

        APIError apiError = APIError.builder()
                .message(exception.getMessage())
                .errorRecordedTime(returnCurrentDateTime())
                .httpStatus(HttpStatus.FORBIDDEN)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

//    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIError> internalServerError(Exception exception) {

        System.out.println("aldhalfhalf");

        //using lombok builder
        APIError apiError = APIError.builder()
                .message(exception.getMessage())
                .errorRecordedTime(returnCurrentDateTime())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //returns current date and time
    String returnCurrentDateTime(){
       return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
    }
}