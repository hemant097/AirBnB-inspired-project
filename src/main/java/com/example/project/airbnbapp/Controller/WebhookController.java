package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.Service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "webhook")
@RequiredArgsConstructor
public class WebhookController {

    //after we do this in stripe cli - stripe listen --forward-to localhost:8080/api/v1/webhook/payment
    //Stripe CLI listens for the event it received from Stripe server and forwards to our localhost app
    //This endpoint waits for payment updates, In the post request, there is payload, signature. We will verify using this
    // signature, that only Stripe is calling this endpoint and no one else.

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader){
        try{
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            paymentService.captureEvent(event);
            return ResponseEntity.noContent().build();
        }catch (SignatureVerificationException e){
            throw new RuntimeException(e);
        }
    }
 }
