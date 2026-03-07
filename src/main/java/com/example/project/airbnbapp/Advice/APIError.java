package com.example.project.airbnbapp.Advice;


import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
@Getter
public class APIError {
    private  HttpStatus httpStatus;
    private  String message;
    private  String errorRecordedTime;
    private List<String> subErrors;
}