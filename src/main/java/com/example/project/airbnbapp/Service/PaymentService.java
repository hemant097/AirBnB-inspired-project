package com.example.project.airbnbapp.Service;

import com.stripe.model.Event;

public interface PaymentService {

    String initiatePayment(Long bookingId);

    void captureEvent(Event event);
}
