package com.example.project.airbnbapp.DTOs;

import com.example.project.airbnbapp.Entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor@NoArgsConstructor
public class HotelPriceDto {

    private Hotel hotel;
    private Double price;
}
