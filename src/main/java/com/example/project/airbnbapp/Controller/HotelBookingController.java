package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.BookingDto;
import com.example.project.airbnbapp.DTOs.BookingRequest;
import com.example.project.airbnbapp.DTOs.GuestDto;
import com.example.project.airbnbapp.Service.BookingService;
import com.example.project.airbnbapp.Service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    @Operation(summary = "Initiate the booking", tags = {"Booking Flow"})
    ResponseEntity<BookingDto> initializeBooking (@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/guests")
    @Operation(summary = "Add guest Ids to the booking", tags = {"Booking Flow"})
    ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
                                         @RequestBody List<Long> guestIdList){

        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestIdList));
    }

    @PostMapping("/{bookingId}/payments")
    @Operation(summary = "Initiate payments flow for the booking", tags = {"Booking Flow"})
    ResponseEntity<Map<String,String>> initiatePayment(@PathVariable Long bookingId){

        String sessionUrl = paymentService.initiatePayment(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }


    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel the booking", tags = {"Booking Flow"})
    ResponseEntity<Void> addGuests(@PathVariable Long bookingId){

        bookingService.cancelBookingWithId(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}/status")
    @Operation(summary = "Check the status of the booking", tags = {"Booking Flow"})
    ResponseEntity<Map<String,String>> getBookingStatus(@PathVariable Long bookingId){

        return ResponseEntity.ok(Collections.singletonMap("status",bookingService.getBookingStatus(bookingId)));
    }




}
