package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.DTOs.RoomDto;
import com.example.project.airbnbapp.Entity.Room;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId, RoomDto roomDto);

    List<RoomDto> getAllRoomsInAHotel(Long HotelId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long roomId);

    Room returnRoomIfExists(Long roomId);

    RoomDto updateRoomById(Long roomId, Long hotelId, RoomDto roomDto);
}
