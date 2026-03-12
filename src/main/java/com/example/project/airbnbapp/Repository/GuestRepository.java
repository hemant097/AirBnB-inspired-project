package com.example.project.airbnbapp.Repository;

import com.example.project.airbnbapp.Entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
