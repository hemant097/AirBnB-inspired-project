package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.BookingDto;
import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.DTOs.HotelReportDto;
import com.example.project.airbnbapp.Service.BookingService;
import com.example.project.airbnbapp.Service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class AdminHotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    //create a new hotel
    @PostMapping
    @Operation(summary = "Create a new hotel", tags = {"Admin Hotel"})
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        log.info("Attempting to create a hotel with name:{}",hotelDto.getName());
        HotelDto savedHotel = hotelService.createNewHotel(hotelDto);

        return new ResponseEntity<>(savedHotel, HttpStatus.CREATED);
    }

    //get a hotel by id
    @GetMapping(path = "/{hotelId}")
    @Operation(summary = "Get a hotel by Id", tags = {"Admin Hotel"})
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId){

        HotelDto savedHotel = hotelService.getHotelById(hotelId);
        return new ResponseEntity<>(savedHotel, HttpStatus.OK);
    }

    //get all hotels of the logged in HOTEL_MANAGER
    @GetMapping
    @Operation(summary = "Get all hotels owned by admin", tags = {"Admin Hotel"})
    public ResponseEntity<List<HotelDto>> getAllHotels(){

        List<HotelDto> hotels = hotelService.getAllHotels();
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    //update the hotel of a particular HOTEL_MANAGER
    @PutMapping(path = "/{hotelId}")
    @Operation(summary = "Update a hotel", tags = {"Admin Hotel"})
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId,
                                                    @RequestBody HotelDto hotelDto){

        HotelDto savedHotel = hotelService.updateHotelById(hotelId, hotelDto);
        return new ResponseEntity<>(savedHotel, HttpStatus.OK);
    }

    //delete the hotel of a particular HOTEL_MANAGER
    @DeleteMapping(path = "/{hotelId}")
    @Operation(summary = "Delete a hotel", tags = {"Admin Hotel"})
    public ResponseEntity<HotelDto> deleteHotelById(@PathVariable Long hotelId){

         hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    //activate the hotel of a particular HOTEL_MANAGER
    @PatchMapping(path = "/{hotelId}/activate")
    @Operation(summary = "Activate a hotel", tags = {"Admin Hotel"})
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId){

        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    //getting all the hotels of the logged in HOTEL_MANAGER
    @GetMapping(path = "/{hotelId}/bookings")
    @Operation(summary = "Get all bookings of a hotel", tags = {"Admin Bookings"})
    public ResponseEntity<List<BookingDto>> getAllBookingsByHotelId(@PathVariable Long hotelId){

        List<BookingDto> hotelBookings = bookingService.getAllBookingsOfAHotel(hotelId);
        return new ResponseEntity<>(hotelBookings, HttpStatus.OK);
    }

    //getting the hotel report of a particular hotel
    @GetMapping(path = "{hotelId}/reports")
    @Operation(summary = "Generate a booking report of a hotel", tags = {"Admin Bookings"})
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId,
                                                         @RequestParam(required = false)LocalDate startDate,
                                                         @RequestParam(required = false)LocalDate endDate){

        if(startDate == null) startDate = LocalDate.now().minusMonths(1);
        if(endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId, startDate, endDate));

    }

}
