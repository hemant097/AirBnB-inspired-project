package com.example.project.airbnbapp.Repository;

import com.example.project.airbnbapp.Entity.Guest;
import com.example.project.airbnbapp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);
}
