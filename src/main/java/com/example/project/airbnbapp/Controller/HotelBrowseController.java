package com.example.project.airbnbapp.Controller;


import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.DTOs.HotelSearchRequest;
import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelBrowseController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest searchRequest){

        Page<HotelDto> page = inventoryService.searchHotels(searchRequest);

        return ResponseEntity.ok(page);
    }
}
