package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.Entity.Booking;

public interface CheckoutService {

    String getCheckOutSession(Booking booking, String successUrl, String failureUrl);
}
