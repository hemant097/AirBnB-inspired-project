package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.Service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class AdminHotelController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        log.info("Attempting to create a hotel with name:{}",hotelDto.getName());
        HotelDto savedHotel = hotelService.createNewHotel(hotelDto);

        return new ResponseEntity<>(savedHotel, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId){

        HotelDto savedHotel = hotelService.getHotelById(hotelId);

        return new ResponseEntity<>(savedHotel, HttpStatus.OK);
    }

    @PutMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId,
                                                    @RequestBody HotelDto hotelDto){

        HotelDto savedHotel = hotelService.updateHotelById(hotelId, hotelDto);

        return new ResponseEntity<>(savedHotel, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDto> deleteHotelById(@PathVariable Long hotelId){

         hotelService.deleteHotelById(hotelId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{hotelId}/activate")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId){

        hotelService.activateHotel(hotelId);

        return ResponseEntity.noContent().build();
    }
}
