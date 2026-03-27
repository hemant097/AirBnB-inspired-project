package com.example.project.airbnbapp.util;

import com.example.project.airbnbapp.Entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public class AppUtil {

    public static User returnCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    //checks if the booking created time plus 10 minutes were before than the current time,
    // if yes booking is expired, and exception is thrown
    public static boolean hasBookingExpired(LocalDateTime bct){
        return bct.plusMinutes(10).isBefore(LocalDateTime.now());
    }
}
