package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.DTOs.BookingDto;
import com.example.project.airbnbapp.DTOs.BookingRequest;
import com.example.project.airbnbapp.DTOs.GuestDto;
import com.example.project.airbnbapp.Entity.Booking;

import java.util.List;

public interface BookingService {

    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long hotelId, List<GuestDto> guestDtoList);

    void cancelBookingWithId(Long bookingId);

    String getBookingStatus(Long bookingId);

    Booking returnBookingIfExists(Long bookingId);
}
