package com.example.project.airbnbapp.Advice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class APIResponse<T> {

    @JsonFormat(pattern = "hh:mm:ss dd-MMM-YYYY")
    private LocalDateTime timeStamp;
    private T data;
    private APIError apiError;

    public APIResponse(){
        this.timeStamp = LocalDateTime.now();
    }

    public APIResponse(T data){
        this();
        this.data = data;
    }

    public APIResponse(APIError error){
        this();
        this.apiError = error;
    }
}

