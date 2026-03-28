package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.DTOs.BookingDto;
import com.example.project.airbnbapp.DTOs.BookingRequest;
import com.example.project.airbnbapp.DTOs.GuestDto;
import com.example.project.airbnbapp.DTOs.HotelReportDto;
import com.example.project.airbnbapp.Entity.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long hotelId, List<Long> guestIdList);

    void cancelBookingWithId(Long bookingId);

    String getBookingStatus(Long bookingId);

    Booking returnBookingIfExists(Long bookingId);

    List<BookingDto> getAllBookingsOfAHotel(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();
}
