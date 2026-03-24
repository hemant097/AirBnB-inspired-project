package com.example.project.airbnbapp.Service.Impl;

import com.example.project.airbnbapp.Entity.Booking;
import com.example.project.airbnbapp.Entity.User;
import com.example.project.airbnbapp.Repository.BookingRepository;
import com.example.project.airbnbapp.Service.CheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.example.project.airbnbapp.util.AppUtil.returnCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final BookingRepository bookingRepo;

    @Override
    public String getCheckOutSession(Booking booking, String successUrl, String failureUrl) {
        log.info("Creating session for booking ID:{}", booking.getId());
        User currentUser = returnCurrentUser();

        try {

            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(currentUser.getName())
                    .setEmail(currentUser.getEmail())
                    .build();

            Customer customer = Customer.create(customerCreateParams);

            SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCurrency("inr")
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem( //adding line item
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData( //adding price data inside line item
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("inr")
                                                .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) //amount in paisas
                                                .setProductData( //adding product data inside price data
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(booking.getHotel().getName()+" : " +booking.getRoom().getType())
                                                                .setDescription("Booking ID:"+booking.getId())
                                                                .build())
                                                    .build())
                                    .build())
                    .build();


            Session session = Session.create(sessionCreateParams);

            booking.setPaymentSessionId(session.getId());
            bookingRepo.save(booking);

            log.info("Session created successfully for booking ID:{}", booking.getId());
            return  session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }
}