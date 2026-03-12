package com.example.project.airbnbapp.DTOs;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String email;
    private String name;
}
