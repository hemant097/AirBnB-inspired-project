package com.example.project.airbnbapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceInfoDto {

    private HotelDto hotelDto;
    private Double price;
}
