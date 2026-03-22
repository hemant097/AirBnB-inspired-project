package com.example.project.airbnbapp.Controller;

import com.example.project.airbnbapp.DTOs.PaymentNote;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/payments")
public class PaymentSuccessFailureController {


    @GetMapping("/success")
    ResponseEntity<PaymentNote> successfulPayment(){
        return ResponseEntity.ok(new PaymentNote("Payment successful, yippee"));
    }


    @GetMapping("/failure")
    ResponseEntity<PaymentNote> failedPayment(){
        return ResponseEntity.ok(new PaymentNote("Payment failed, try again"));
    }
}
