package com.example.project.airbnbapp.Controller;


import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.DTOs.HotelInfoDto;
import com.example.project.airbnbapp.DTOs.HotelSearchRequest;
import com.example.project.airbnbapp.Service.HotelService;
import com.example.project.airbnbapp.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest searchRequest){

        Page<HotelDto> page = inventoryService.searchHotels(searchRequest);

        return ResponseEntity.ok(page);
    }

    @GetMapping(path = "/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoWithRooms(hotelId));
    }
}
