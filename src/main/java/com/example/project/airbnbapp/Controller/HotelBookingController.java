package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.BookingDto;
import com.example.project.airbnbapp.DTOs.BookingRequest;
import com.example.project.airbnbapp.DTOs.GuestDto;
import com.example.project.airbnbapp.Service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    ResponseEntity<BookingDto> initializeBooking (@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/guests")
    ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
                                         @RequestBody List<GuestDto> guestDtoList){

        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }
}
