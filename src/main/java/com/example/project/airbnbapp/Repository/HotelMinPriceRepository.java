package com.example.project.airbnbapp.Repository;

import com.example.project.airbnbapp.DTOs.HotelPriceDto;
import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Entity.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice,Long> {

    @Query("""
        SELECT new com.example.project.airbnbapp.DTOs.HotelPriceDto (hm.hotel,AVG(hm.price)) 
            from HotelMinPrice hm
        WHERE hm.hotel.city=:city
            AND (hm.date between :startDate AND :endDate)
            AND hm.hotel.active=true
        GROUP BY hm.hotel
    """)
    Page<HotelPriceDto> findHotelsWithAvailableInventory(@Param("city") String city,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate,
                                                         @Param("roomCount") Integer roomCount,
                                                         @Param("dateCount") Long dateCount,
                                                         Pageable pageable);


    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
