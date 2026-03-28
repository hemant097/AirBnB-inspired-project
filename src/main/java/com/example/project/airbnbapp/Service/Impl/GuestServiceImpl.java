package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.DTOs.GuestDto;
import com.example.project.airbnbapp.Entity.Guest;
import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Repository.GuestRepository;
import com.example.project.airbnbapp.Service.GuestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.project.airbnbapp.util.AppUtil.returnCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {

    private final ModelMapper modelMapper;
    private final GuestRepository guestRepo;

    @Override
    public List<GuestDto> getAllGuests() {
        User currentUser = returnCurrentUser();
        log.info("Fetching all guests of user with id: {}", currentUser.getId());

        List<Guest> guests = guestRepo.findByUser(currentUser);
        return guests.stream()
                .map((element) -> modelMapper.map(element, GuestDto.class))
                .toList();

    }

    @Override
    public GuestDto addNewGuest(GuestDto guestDto) {
        log.info("Adding new guest {}",guestDto);
        User user = returnCurrentUser();

        Guest guest = modelMapper.map(guestDto,Guest.class);
        guest.setUser(user);
        Guest savedGuest = guestRepo.save(guest);

        log.info("Guest added with ID:{}",savedGuest.getId());
        return modelMapper.map(savedGuest, GuestDto.class);
    }

    @Override
    public void updateGuest(Long guestId, GuestDto guestDto) {
        log.info("Updating guest with ID: {}",guestId);
        Guest guest = returnGuestIfExists(guestId);

        User user = returnCurrentUser();
        if(!user.equals(guest.getUser()))
            throw new AccessDeniedException("You do not have privilege to update this guest's info");

        modelMapper.map(guestDto, guest);
        guest.setUser(user);
        guest.setId(guestId);
        guestRepo.save(guest);

        log.info("Guest with ID:{} updated successfully",guestId);
    }

    @Override
    public void deleteGuest(Long guestId) {
        log.info("Deleting guest with ID: {}",guestId);
        Guest guest = returnGuestIfExists(guestId);

        User user = returnCurrentUser();
        if(!user.equals(guest.getUser()))
            throw new AccessDeniedException("You do not have privilege to delete this guest's info");

        guestRepo.deleteById(guestId);
        log.info("Guest with ID:{} deleted successfully",guestId);

    }

    //return a guest if exists, else throws exception
    Guest returnGuestIfExists(Long guestId){
        return guestRepo.findById(guestId)
                .orElseThrow( () -> new EntityNotFoundException("Guest not found with ID "+guestId));
    }
}
