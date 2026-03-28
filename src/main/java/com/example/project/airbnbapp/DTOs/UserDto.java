package com.example.project.airbnbapp.DTOs;

import com.example.project.airbnbapp.Entity.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;

}
