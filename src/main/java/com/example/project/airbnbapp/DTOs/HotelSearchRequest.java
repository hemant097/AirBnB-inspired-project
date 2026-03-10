package com.example.project.airbnbapp.DTOs;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequest {

    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer roomsRequired;
    private Integer page=0;
    private Integer size=10;
}
