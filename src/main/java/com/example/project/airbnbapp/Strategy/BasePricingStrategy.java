package com.example.project.airbnbapp.Strategy;

import com.example.project.airbnbapp.Entity.Inventory;

import java.math.BigDecimal;

public class BasePricingStrategy implements PricingStrategy{

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
