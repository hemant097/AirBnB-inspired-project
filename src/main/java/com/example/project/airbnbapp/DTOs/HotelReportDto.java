package com.example.project.airbnbapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class HotelReportDto {
    private Long confirmedBookings;
    private BigDecimal totalRevenue;
    private BigDecimal averageRevenue;
}
