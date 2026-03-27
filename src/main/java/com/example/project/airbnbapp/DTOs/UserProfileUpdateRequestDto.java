package com.example.project.airbnbapp.DTOs;

import com.example.project.airbnbapp.Entity.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileUpdateRequestDto {
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;

}
