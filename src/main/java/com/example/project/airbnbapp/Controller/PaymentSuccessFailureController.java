package com.example.project.airbnbapp.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/payments")
public class PaymentSuccessFailureController {

    //Thymeleaf is used here,

    @GetMapping("/success")
    String successfulPayment(Model model){
        model.addAttribute("message", "Payment successful, yippee");
        return "afterPayment";
    }


    @GetMapping("/failure")
    String failedPayment(Model model){
        model.addAttribute("message", "Payment failed, please try again");
        return "afterPayment";
    }
}
