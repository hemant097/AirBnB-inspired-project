package com.example.project.airbnbapp.Repository;

import com.example.project.airbnbapp.Entity.Inventory;
import com.example.project.airbnbapp.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    void deleteInventoriesByRoom(Room room);
}
