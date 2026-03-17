package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.DTOs.HotelInfoDto;
import com.example.project.airbnbapp.DTOs.RoomDto;
import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Entity.Room;
import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Exception.ConflictException;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Exception.UnauthorizedException;
import com.example.project.airbnbapp.Repository.HotelRepository;
import com.example.project.airbnbapp.Repository.RoomRepository;
import com.example.project.airbnbapp.Service.HotelService;
import com.example.project.airbnbapp.Service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepo;
    private final RoomRepository roomRepo;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;



    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("creating a new hotel with name: {}",hotelDto.getName());

        Hotel hotelToSave  = modelMapper.map(hotelDto,Hotel.class);
        hotelToSave.setActive(false);

        isCurrentUserOwnerOfThisHotel(hotelToSave);

        Hotel savedHotel = hotelRepo.save(hotelToSave);
        log.info("created a new hotel with id: {}",savedHotel.getId());
        return modelMapper.map(savedHotel,HotelDto.class);

    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("getting the hotel with id: {}",id);

        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id:"+id));

        isCurrentUserOwnerOfThisHotel(hotel);
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("updating the hotel with id: {}",id);

        Hotel hotelToUpdate = returnHotelIfExists(id);

        isCurrentUserOwnerOfThisHotel(hotelToUpdate);

        modelMapper.map(hotelDto, hotelToUpdate);
        hotelToUpdate.setId(id);
        Hotel updatedHotel = hotelRepo.save(hotelToUpdate);
        return modelMapper.map(updatedHotel, HotelDto.class);



    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {

        log.info("deleting a hotel with id: {}",id);

       Hotel hotelToDelete = returnHotelIfExists(id);

        isCurrentUserOwnerOfThisHotel(hotelToDelete);

        //delete the future inventories for this hotel
       for(Room room : hotelToDelete.getRooms()){
           inventoryService.deleteFutureInventory(room);
           roomRepo.deleteById(room.getId());
       }


        hotelRepo.deleteById(id);

    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {

        log.info("trying to activating a new hotel with id: {}",hotelId);

        Hotel hotelToActivate = returnHotelIfExists(hotelId);

        isCurrentUserOwnerOfThisHotel(hotelToActivate);

        if(hotelToActivate.getActive())
            throw new ConflictException("this hotel is already activated, id: "+hotelId);
        else
            hotelToActivate.setActive(true);

        //create inventory for all the rooms
        //assuming only do it once
        for(Room room : hotelToActivate.getRooms())
            inventoryService.initializeRoomForAYear(room);

    }

    //Public method
    @Override
    public HotelInfoDto getHotelInfoWithRooms(Long hotelId) {
        Hotel hotel = returnHotelIfExists(hotelId);

        List< RoomDto> roomDtoList = hotel.getRooms().stream()
                .map( (element) -> modelMapper.map(element, RoomDto.class))
                .toList();
       return new HotelInfoDto( modelMapper.map(hotel, HotelDto.class), roomDtoList);
    }

    //Returns the hotel for an Id, else throws a ResourceNotFoundException
    Hotel returnHotelIfExists(Long hotelId){
        return hotelRepo.findById(hotelId)
                .orElseThrow( () -> new ResourceNotFoundException("No hotel exists with id :" +hotelId));
    }

    //checks whether current user in security context is owner of the hotel, else throws exception
    void isCurrentUserOwnerOfThisHotel(Hotel hotel){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!user.equals(hotel.getOwner()))
            throw new UnauthorizedException("User does not own this hotel with id:"+hotel.getId());
    }
}
