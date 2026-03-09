package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.Entity.Inventory;
import com.example.project.airbnbapp.Entity.Room;
import com.example.project.airbnbapp.Repository.InventoryRepository;
import com.example.project.airbnbapp.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepo;

    @Override
    public void initializeRoomForAYear(Room room) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        while (!today.isAfter(endDate)){

            Inventory inventoryForAParticularDate = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .price(room.getBasePrice())
                    .city(room.getHotel().getCity())
                    .date(today)
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .bookedCount(0)
                    .closed(false)
                    .build();

            inventoryRepo.save(inventoryForAParticularDate);

            today=today.plusDays(1);
        }


    }

    @Override
    public void deleteFutureInventory(Room room) {
            inventoryRepo.deleteInventoriesByRoom(room );
    }
}
