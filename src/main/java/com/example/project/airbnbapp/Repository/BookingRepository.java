package com.example.project.airbnbapp.Repository;

import com.example.project.airbnbapp.Entity.Booking;
import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findBookingByBookingStatusNot(BookingStatus bookingStatus);

    List<Booking> findBookingByHotelId(Long hotelId);

    @Query("""
        SELECT b from Booking b
        WHERE b.hotel = :hotel
            AND b.bookingStatus = :status
            AND (b.createdAt between :start AND :end)
    """)
    List<Booking> findBookingsByHotelAndStatusAndCreatedAtBetween(@Param("hotel")Hotel hotel,
                                                                @Param("start")LocalDateTime startDateTime,
                                                                @Param("end")LocalDateTime endDateTime,
                                                                @Param("status") BookingStatus bookingStatus
    );
}
