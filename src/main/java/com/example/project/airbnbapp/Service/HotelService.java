package com.example.project.airbnbapp.Service;


import com.example.project.airbnbapp.DTOs.BookingDto;
import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.DTOs.HotelInfoDto;
import com.example.project.airbnbapp.Entity.Hotel;

import java.util.List;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoWithRooms(Long hotelId);

    Hotel returnHotelIfExists(Long hotelId);

    public void isCurrentUserOwnerOfThisHotel(Hotel hotel);

    List<HotelDto> getAllHotels();
}
