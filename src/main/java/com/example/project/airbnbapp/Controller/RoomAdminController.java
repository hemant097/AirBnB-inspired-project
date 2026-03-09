package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.DTOs.RoomDto;
import com.example.project.airbnbapp.Entity.Room;
import com.example.project.airbnbapp.Service.HotelService;
import com.example.project.airbnbapp.Service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;

    @PostMapping
    ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId,
                                       @RequestBody RoomDto roomDto){

        return new ResponseEntity<>(roomService.createNewRoom(hotelId, roomDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms(@PathVariable Long hotelId){
        return ResponseEntity.ok(roomService.getAllRoomsInAHotel(hotelId));
    }

    @GetMapping(path = "/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId){
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping(path = "/{roomId}")
    public ResponseEntity<Void> deleteARoomById(@PathVariable Long roomId){
     roomService.deleteRoomById(roomId);
     return ResponseEntity.noContent().build();
    }





}
