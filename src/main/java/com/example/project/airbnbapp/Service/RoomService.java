package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.DTOs.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId, RoomDto roomDto);

    List<RoomDto> getAllRoomsInAHotel(Long HotelId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long roomId);
}
