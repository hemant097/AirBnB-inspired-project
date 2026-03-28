package com.example.project.airbnbapp.Controller;


import com.example.project.airbnbapp.DTOs.BookingDto;
import com.example.project.airbnbapp.DTOs.GuestDto;
import com.example.project.airbnbapp.DTOs.UserDto;
import com.example.project.airbnbapp.DTOs.UserProfileUpdateRequestDto;
import com.example.project.airbnbapp.Service.BookingService;
import com.example.project.airbnbapp.Service.GuestService;
import com.example.project.airbnbapp.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @PatchMapping("/update")
    @Operation(summary = "Update the user profile", tags = {"Profile"})
    public ResponseEntity<Void> updateProfile(@RequestBody UserProfileUpdateRequestDto dto){
        userService.updateProfile(dto);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings")
    @Operation(summary = "Get all my previous bookings", tags = {"Profile"})
    public ResponseEntity<List<BookingDto>> getUserBookings (){
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("profile")
    @Operation(summary = "Get my profile", tags = {"Profile"})
    public ResponseEntity<UserDto> getMyProfile(){
        return new ResponseEntity<>(userService.getMyProfile(), HttpStatus.OK);
    }

    @GetMapping("/guests")
    @Operation(summary = "Get all my guests", tags = {"Booking Guests"})
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @PostMapping("/guests")
    @Operation(summary = "Add a new guest to my guests list", tags = {"Booking Guests"})
    public ResponseEntity<GuestDto> addNewGuest(@RequestBody GuestDto guestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.addNewGuest(guestDto));
    }

    @PutMapping("guests/{guestId}")
    @Operation(summary = "Update a guest", tags = {"Booking Guests"})
    public ResponseEntity<Void> updateGuest(@PathVariable Long guestId, @RequestBody GuestDto guestDto) {
        guestService.updateGuest(guestId, guestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guests/{guestId}")
    @Operation(summary = "Remove a guest", tags = {"Booking Guests"})
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }
}
