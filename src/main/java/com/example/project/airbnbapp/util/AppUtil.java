package com.example.project.airbnbapp.util;

import com.example.project.airbnbapp.Entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtil {

    public static User returnCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
