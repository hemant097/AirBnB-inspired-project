package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.RoomDto;
import com.example.project.airbnbapp.Service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Create a new room in a hotel", tags = {"Admin Inventory"})
    ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId,
                                       @RequestBody RoomDto roomDto){

        return new ResponseEntity<>(roomService.createNewRoom(hotelId, roomDto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all rooms in a hotel", tags = {"Admin Inventory"})
    public ResponseEntity<List<RoomDto>> getAllRooms(@PathVariable Long hotelId){
        return ResponseEntity.ok(roomService.getAllRoomsInAHotel(hotelId));
    }

    @GetMapping(path = "/{roomId}")
    @Operation(summary = "Get a room by id", tags = {"Admin Inventory"})
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId, @PathVariable Long hotelId){
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @DeleteMapping(path = "/{roomId}")
    @Operation(summary = "Delete a room by id", tags = {"Admin Inventory"})
    public ResponseEntity<Void> deleteARoomById(@PathVariable Long roomId, @PathVariable Long hotelId){
         roomService.deleteRoomById(roomId);
         return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roomId}")
    @Operation(summary = "Update a room", tags = {"Admin Inventory"})
    public ResponseEntity<RoomDto> updateRoomById(@PathVariable Long hotelId, @PathVariable Long roomId,
                                                  @RequestBody RoomDto roomDto){
        return ResponseEntity.ok(roomService.updateRoomById(roomId,hotelId,roomDto));
    }






}
