package com.example.project.airbnbapp.DTOs;

import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {

    private Long id;

    private String name;

    private Gender gender;

    private Integer age;
}
