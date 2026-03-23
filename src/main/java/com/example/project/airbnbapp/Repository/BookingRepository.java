package com.example.project.airbnbapp.Repository;

import com.example.project.airbnbapp.Entity.Booking;
import com.example.project.airbnbapp.Entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findBookingByBookingStatusNot(BookingStatus bookingStatus);
}
