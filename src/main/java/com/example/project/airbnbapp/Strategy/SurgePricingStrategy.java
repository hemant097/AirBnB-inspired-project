package com.example.project.airbnbapp.Strategy;

import com.example.project.airbnbapp.Entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal basePrice = wrapped.calculatePrice(inventory);
        return basePrice.multiply(inventory.getSurgeFactor());

    }
}
