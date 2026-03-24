package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.DTOs.RoomDto;
import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Entity.Room;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Repository.RoomRepository;
import com.example.project.airbnbapp.Service.HotelService;
import com.example.project.airbnbapp.Service.InventoryService;
import com.example.project.airbnbapp.Service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepo;
    private final ModelMapper modelMapper;
    private final HotelService hotelService;
    private final InventoryService inventoryService;


    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel with id: {}",hotelId);
        Hotel hotel = hotelService.returnHotelIfExists(hotelId);

        hotelService.isCurrentUserOwnerOfThisHotel(hotel);

        Room roomToCreate = modelMapper.map(roomDto, Room.class);
        roomToCreate.setHotel(hotel);

        Room createdRoom = roomRepo.save(roomToCreate);

        if(hotel.getActive())
            inventoryService.initializeRoomForAYear(createdRoom);

        return modelMapper.map(createdRoom, RoomDto.class);


    }

    @Override
    public List<RoomDto> getAllRoomsInAHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with id: {}",hotelId);
        Hotel hotel = hotelService.returnHotelIfExists(hotelId);
        hotelService.isCurrentUserOwnerOfThisHotel(hotel);

        return hotel.getRooms().stream()
                .map( element -> modelMapper.map(element, RoomDto.class))
                .toList();
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room with ID :{}",roomId);
        Room room = returnRoomIfExists(roomId);

        return modelMapper.map(room,RoomDto.class);

    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID :{}",roomId);
        Room room = returnRoomIfExists(roomId);

        hotelService.isCurrentUserOwnerOfThisHotel(room.getHotel());

        inventoryService.deleteFutureInventory(room);
        roomRepo.delete(room);
    }

    //Returns the Room for an Id, else throws a ResourceNotFoundException
    @Override
    public Room returnRoomIfExists(Long roomId){
        return roomRepo.findById(roomId)
                .orElseThrow( () -> new ResourceNotFoundException("No room exists with id :"+roomId));
    }
}
