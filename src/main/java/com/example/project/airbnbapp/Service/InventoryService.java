package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.DTOs.HotelPriceDto;
import com.example.project.airbnbapp.DTOs.HotelSearchRequest;
import com.example.project.airbnbapp.DTOs.InventoryDto;
import com.example.project.airbnbapp.Entity.Inventory;
import com.example.project.airbnbapp.Entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteFutureInventory(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest searchRequest);

    List<InventoryDto> allInventoryForARoom(Long roomId);

    List<InventoryDto> updateInventoryOfARoom(Long roomId);
}
