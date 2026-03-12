package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.DTOs.HotelDto;
import com.example.project.airbnbapp.DTOs.HotelSearchRequest;
import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Entity.Inventory;
import com.example.project.airbnbapp.Entity.Room;
import com.example.project.airbnbapp.Repository.InventoryRepository;
import com.example.project.airbnbapp.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepo;
    private final ModelMapper modelMapper;

    @Override
    public void initializeRoomForAYear(Room room) {

        log.info("initializing inventory for this room with id:{}, in :{}",room.getId(), room.getHotel().getName());

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
                    .reservedCount(0)
                    .closed(false)
                    .build();

            inventoryRepo.save(inventoryForAParticularDate);

            today=today.plusDays(1);
        }

        log.info("initialized inventory for this room with id:{}, in :{}",room.getId(), room.getHotel().getName());

    }

    @Override
    public void deleteFutureInventory(Room room) {
        log.info("Deleting the inventories of room with id:{}", room.getId());
            inventoryRepo.deleteInventoriesByRoom(room );
    }

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest searchRequest) {

        log.info("Searching hotels for {} city, from {} to {} ",
                searchRequest.getCity(), searchRequest.getStartDate(), searchRequest.getEndDate());

        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());

        long dateCount = ChronoUnit.DAYS.between(searchRequest.getStartDate(), searchRequest.getEndDate()) +1;

        Page<Hotel> page = inventoryRepo.findHotelsWithAvailableInventory(
                searchRequest.getCity(),
                searchRequest.getStartDate(), searchRequest.getEndDate(),
                searchRequest.getRoomsRequired(),
                dateCount,
                pageable
        );

       List<Hotel> hotels =  page.getContent();

        System.out.println(hotels.size());

        return page.map( element -> modelMapper.map(element, HotelDto.class));
    }
}
