package com.example.project.airbnbapp.DTOs;

import com.example.project.airbnbapp.Entity.Guest;
import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Entity.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {

    private Long id;

    private UserDto userDto;

    private Integer roomsCount;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private BookingStatus bookingStatus;

    private Set<GuestDto> guests;

    private BigDecimal amount;
}
