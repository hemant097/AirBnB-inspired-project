package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.Entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteFutureInventory(Room room);


}
