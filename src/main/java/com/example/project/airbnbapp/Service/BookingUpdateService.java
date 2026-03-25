package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.Entity.Booking;
import com.example.project.airbnbapp.Entity.Guest;
import com.example.project.airbnbapp.Entity.enums.BookingStatus;
import com.example.project.airbnbapp.Repository.BookingRepository;
import com.example.project.airbnbapp.Repository.GuestRepository;
import com.example.project.airbnbapp.Repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingUpdateService {

    private final BookingRepository bookingRepo;
    private final InventoryRepository inventoryRepo;
    private final GuestRepository guestRepo;

    //Here all the booking which are not confirmed, get deleted if the last updated time is more than 7 days i.e., all
    // the non-confirmed bookings older than 7 days get deleted automatically


    @Scheduled(cron = "0 0 * * * *")
    public void updateBookings(){

        List<Booking> bookingList = bookingRepo.findBookingByBookingStatusNot(BookingStatus.CONFIRMED);

        for(Booking booking : bookingList){
            if(isBookingWorthyOfDeletion(booking.getUpdatedAt())){
                Long bookingId = booking.getId();
                //deleting guests related to this booking
                for( Guest guest : booking.getGuests()) {
                    guestRepo.deleteById(guest.getId());
                    log.info("Guest ID:{} related to booking ID:{} has been deleted", guest.getId(), bookingId);
                }

                //deleting the booking
                bookingRepo.deleteById(booking.getId());
                log.info("This booking is being deleted, with booking ID:{}", bookingId);

                //updating the booked_count related to this booking in inventory
                inventoryRepo.updateInventoryWhenBookingDeleted(booking.getRoom().getId(),
                        booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());
                log.info("Inventory related to booking ID:{} is also updated",bookingId);
            }
        }
    }

    boolean isBookingWorthyOfDeletion(LocalDateTime bookingUpdateTime){
        return bookingUpdateTime.plusDays(7).isBefore(LocalDateTime.now());
    }
}
