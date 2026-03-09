package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.DTOs.RoomDto;
import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Entity.Room;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Repository.HotelRepository;
import com.example.project.airbnbapp.Repository.RoomRepository;
import com.example.project.airbnbapp.Service.RoomService;
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
    private final HotelRepository hotelRepo;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel with id: {}",hotelId);
        Hotel hotel = returnHotelIfExists(hotelId);
        Room roomToCreate = modelMapper.map(roomDto, Room.class);
        roomToCreate.setHotel(hotel);

        Room createdRoom = roomRepo.save(roomToCreate);

        return modelMapper.map(createdRoom, RoomDto.class);


    }

    @Override
    public List<RoomDto> getAllRoomsInAHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with id: {}",hotelId);

        Hotel hotel = returnHotelIfExists(hotelId);
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
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID :{}",roomId);

        Room room = returnRoomIfExists(roomId);

        roomRepo.delete(room);
    }

    Hotel returnHotelIfExists(Long id){
        return hotelRepo.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("No hotel exists with id :" +id));
    }

    Room returnRoomIfExists(Long roomId){
        return roomRepo.findById(roomId)
                .orElseThrow( () -> new ResourceNotFoundException("No room exists with id :"+roomId));
    }
}
