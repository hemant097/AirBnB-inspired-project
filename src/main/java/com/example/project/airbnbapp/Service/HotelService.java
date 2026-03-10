package com.example.project.airbnbapp.Service;


import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.DTOs.HotelInfoDto;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoWithRooms(Long hotelId);
}
