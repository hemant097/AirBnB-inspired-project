package com.example.project.airbnbapp.Exception;

public class UnauthorizedException  extends RuntimeException{
    public UnauthorizedException(String message) {
        super(message);
    }
}
