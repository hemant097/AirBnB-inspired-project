package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.DTOs.*;
import com.example.project.airbnbapp.Entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteFutureInventory(Room room);

    Page<HotelPriceInfoDto> searchHotels(HotelSearchRequest searchRequest);

    List<InventoryDto> allInventoryForARoom(Long roomId);

    void updateInventoryOfARoom(Long roomId, UpdateInventoryRequestDto requestDto);
}
