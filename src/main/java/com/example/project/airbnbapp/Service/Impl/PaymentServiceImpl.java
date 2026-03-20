package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.Entity.Booking;
import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Entity.Room;
import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Entity.enums.BookingStatus;
import com.example.project.airbnbapp.Exception.ResourceNotFoundException;
import com.example.project.airbnbapp.Exception.UnauthorizedException;
import com.example.project.airbnbapp.Repository.BookingRepository;
import com.example.project.airbnbapp.Service.CheckoutService;
import com.example.project.airbnbapp.Service.PaymentService;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.project.airbnbapp.Entity.enums.BookingStatus.PAYMENT_PENDING;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepo;
    private final CheckoutService checkoutService;

    @Value("${front-end.url}")
    private String frontEndUrl;

    @Override
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
    public void captureEvent(Event event) {
        //TODO
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
