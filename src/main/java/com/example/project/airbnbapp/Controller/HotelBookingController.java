package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.BookingDto;
import com.example.project.airbnbapp.DTOs.BookingRequest;
import com.example.project.airbnbapp.DTOs.GuestDto;
import com.example.project.airbnbapp.Service.BookingService;
import com.example.project.airbnbapp.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class HotelBookingController {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    @PostMapping("/init")
    ResponseEntity<BookingDto> initializeBooking (@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/guests")
    ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
                                         @RequestBody List<GuestDto> guestDtoList){

        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }

    @PostMapping("/{bookingId}/payments")
    ResponseEntity<Map<String,String>> initiatePayment(@PathVariable Long bookingId){

        String sessionUrl = paymentService.initiatePayment(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }


    @DeleteMapping("/{bookingId}/delete")
    ResponseEntity<Void> addGuests(@PathVariable Long bookingId){

        bookingService.deleteABooking(bookingId);
        return ResponseEntity.noContent().build();
    }





}
