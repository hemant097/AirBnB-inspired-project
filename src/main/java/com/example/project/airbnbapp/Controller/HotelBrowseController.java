package com.example.project.airbnbapp.Controller;


import com.example.project.airbnbapp.DTOs.*;
import com.example.project.airbnbapp.Service.HotelService;
import com.example.project.airbnbapp.Service.InventoryService;
import com.example.project.airbnbapp.Service.PricingUpdateService;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping("/search")
    @Operation(summary = "Search hotels", tags = {"Browse Hotels"})
    public ResponseEntity<Page<HotelPriceInfoDto>> searchHotels(@RequestBody HotelSearchRequest searchRequest){

        Page<HotelPriceInfoDto> page = inventoryService.searchHotels(searchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping(path = "/{hotelId}/info")
    @Operation(summary = "Get a hotel info by hotelId", tags = {"Browse Hotels"})
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoWithRooms(hotelId));
    }

}
