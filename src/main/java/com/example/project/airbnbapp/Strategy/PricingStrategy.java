package com.example.project.airbnbapp.Strategy;

import com.example.project.airbnbapp.Entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//Decorator design pattern
public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
