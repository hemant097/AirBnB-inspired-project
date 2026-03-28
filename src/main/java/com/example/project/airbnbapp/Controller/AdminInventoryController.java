package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.InventoryDto;
import com.example.project.airbnbapp.DTOs.UpdateInventoryRequestDto;
import com.example.project.airbnbapp.Service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/inventory/rooms")
@RequiredArgsConstructor
public class AdminInventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{roomId}")
    @Operation(summary = "Get all inventory of a room", tags = {"Admin Inventory"})
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryService.allInventoryForARoom(roomId));
    }

    @PutMapping("/{roomId}")
    @Operation(summary = "Update the inventory of a room", tags = {"Admin Inventory"})
    public ResponseEntity<Void> updateInventoryByRoom(@PathVariable Long roomId,
                                                                    @RequestBody UpdateInventoryRequestDto requestDto ){
        inventoryService.updateInventoryOfARoom(roomId, requestDto);
        return ResponseEntity.noContent().build();
    }
}
