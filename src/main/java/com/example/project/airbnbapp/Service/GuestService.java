package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.DTOs.GuestDto;

import java.util.List;

public interface GuestService {
    List<GuestDto> getAllGuests();

    GuestDto addNewGuest(GuestDto guestDto);

    void updateGuest(Long guestId, GuestDto guestDto);

    void deleteGuest(Long guestId);
}
