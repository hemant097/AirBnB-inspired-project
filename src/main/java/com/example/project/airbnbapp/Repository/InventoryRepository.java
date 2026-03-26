package com.example.project.airbnbapp.Repository;

import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Entity.Inventory;
import com.example.project.airbnbapp.Entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    void deleteInventoriesByRoom(Room room);

    //As inventory also has reference of hotel,
    //This query matches the city, checks availability between start and end dates, available roomCount>= required
    // and will group by hotelId and roomId, this intermediate aggregated result will have multiple rows for the different dates,
    //now HAVING COUNT will group them accordingly, i.e., it will return those rooms which are available, for all
    //number of days required, i.e., dateCount
    //The grouping by both hotel and room, can be helpful, when a
    @Query("""
        SELECT distinct i.hotel from Inventory i
        WHERE i.city=:city
            AND (i.date between :startDate AND :endDate)
            AND i.closed=false
            AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomCount
        GROUP BY i.hotel, i.room
        HAVING COUNT(distinct i.date)=:dateCount
    """)
    Page<Hotel> findHotelsWithAvailableInventory(@Param("city") String city,
                                                 @Param("startDate")LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("roomCount") Integer roomCount,
                                                 @Param("dateCount") Long dateCount,
                                                 Pageable pageable);

    //lock inventory the requested requirements are matching
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i from Inventory i
        WHERE i.room.id=:roomId
            AND (i.date between :startDate AND :endDate)
            AND i.closed=false
            AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsRequired
    """)
    List<Inventory> findAndLockAvailableInventory(@Param("roomId") Long roomId,
                                                  @Param("startDate")LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("roomsRequired") Integer roomCount );

    //update inventory when a booking is requested
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount + :roomsRequired
                WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsRequired
                AND (i.closed = false )
    """)
    void initBooking(@Param("roomId") Long roomId,
                                @Param("startDate")LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("roomsRequired") Integer roomCount);

    //the confirmBooking method which is being called just after this in PaymentServiceImpl doesn't get executed if
    // there isn't a return value here. Like we use void (as Return value of the method is never used)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i from Inventory i
        WHERE i.room.id=:roomId
            AND (i.date between :startDate AND :endDate)
            AND (i.totalCount - i.bookedCount) >= :roomCount
            AND i.closed=false
    """)
    List<Inventory> lockReservedInventory(@Param("roomId") Long roomId,
                                                  @Param("startDate")LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("roomCount") Integer roomCount );

    //update inventory when a booking is confirmed
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount - :numberOfRooms,
                    i.bookedCount = i.bookedCount + :numberOfRooms
                WHERE i.room.id = :roomId
                AND i.date BETWEEN :checkInDate AND :checkOutDate
                AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                AND (i.reservedCount >= :numberOfRooms)
                AND (i.closed = false )
    """)
    void confirmBooking(@Param("roomId") Long roomId,
                        @Param("checkInDate") LocalDate startDate,
                        @Param("checkOutDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms);


    //update when a booking is cancelled by the user
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.bookedCount = i.bookedCount - :numberOfRooms
                    WHERE i.room.id = :roomId
                AND i.date BETWEEN :checkInDate AND :checkOutDate
                AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                AND (i.closed = false )
    """)
    void updateInventoryWhenBookingCancelled(@Param("roomId") Long roomId,
                                             @Param("checkInDate") LocalDate startDate,
                                             @Param("checkOutDate") LocalDate endDate,
                                             @Param("numberOfRooms") int numberOfRooms);

    //update when a booking is deleted by the Cron job
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount - :numberOfRooms
                    WHERE i.room.id = :roomId
                AND i.date BETWEEN :checkInDate AND :checkOutDate
                AND (i.reservedCount >= :numberOfRooms)
                AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                AND (i.closed = false )
    """)
    void updateInventoryWhenBookingDeleted(@Param("roomId") Long roomId,
                                               @Param("checkInDate") LocalDate startDate,
                                               @Param("checkOutDate") LocalDate endDate,
                                               @Param("numberOfRooms") int numberOfRooms);


    List<Inventory> findAllByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);

    @Query("SELECT i from Inventory i where i.room.id= : roomId order by i.date DESC")
    List<Inventory> findInventoryForAParticularRoom(@Param("roomId") Long roomId);
}

