package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.Entity.Booking;
import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Exception.UnauthorizedException;
import com.example.project.airbnbapp.Repository.BookingRepository;
import com.example.project.airbnbapp.Repository.InventoryRepository;
import com.example.project.airbnbapp.Service.CheckoutService;
import com.example.project.airbnbapp.Service.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.project.airbnbapp.Entity.enums.BookingStatus.CONFIRMED;
import static com.example.project.airbnbapp.Entity.enums.BookingStatus.PAYMENT_PENDING;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepo;
    private final InventoryRepository inventoryRepo;
    private final CheckoutService checkoutService;

    @Value("${front-end.url}")
    private String frontEndUrl;

    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {

        log.info("Initiating payment for booking ID:{}",bookingId);

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("no booking found with id "+bookingId));

        User currentUser = returnCurrentUser();
        if(!currentUser.equals(booking.getUser()))
            throw new UnauthorizedException("Booking does not belong to this user with id:"+currentUser.getId());
        if(  hasBookingExpired(booking.getCreatedAt()) )
            throw new IllegalStateException("Booking has expired, create new booking");

        log.info("Booking is valid, and in RESERVED status, now moving forward with payments");

        String sessionUrl = checkoutService.getCheckOutSession(booking, frontEndUrl+"/payments/success",frontEndUrl+"/payments/failure");

        booking.setBookingStatus(PAYMENT_PENDING);

        log.info("payment session url is generated, booking status is {}",PAYMENT_PENDING);
        bookingRepo.save(booking);
        return sessionUrl;
    }

    @Override
    @Transactional
    public void captureEvent(Event event) {
        if("checkout.session.completed".equals(event.getType())){
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            //if no session present, RETURN from this method
            if(session == null) return;

            String sessionId = session.getId();
            log.info("Capturing event for session ID:{}", sessionId);

            Booking booking = bookingRepo.findByPaymentSessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("no booking found for sessionId:" + sessionId));
            booking.setBookingStatus(CONFIRMED);
            bookingRepo.save(booking);

            Long roomId = booking.getRoom().getId();
            LocalDate checkInDate = booking.getCheckInDate();
            LocalDate checkOutDate = booking.getCheckOutDate();
            Integer roomCount = booking.getRoomsCount();

            log.info("confirming booking in {}, room:{}, from {} to {}, {} rooms",
                    booking.getHotel().getName(), booking.getRoom().getType(), checkInDate, checkOutDate, roomCount);

            inventoryRepo.lockReservedInventory(roomId, checkInDate, checkOutDate, roomCount);
            inventoryRepo.confirmBooking(roomId, checkInDate, checkOutDate, roomCount);

            log.info("Booking confirmed for bookingId:{}",booking.getId());

        }else{
            log.warn("unhandled event type:{}", event.getType());
        }
    }

    //checks if the booking created time plus 10 minutes were before than the current time,
    // if yes booking is expired, and exception is thrown
    boolean hasBookingExpired(LocalDateTime bct){
        return bct.plusMinutes(10).isBefore(LocalDateTime.now());
    }

    User returnCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
