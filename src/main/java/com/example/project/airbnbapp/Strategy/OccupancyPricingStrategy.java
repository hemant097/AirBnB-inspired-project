package com.example.project.airbnbapp.Strategy;

import com.example.project.airbnbapp.Entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        double occupancyRate = (double) inventory.getBookedCount() /inventory.getTotalCount();

        if(occupancyRate > 0.75)
            price = price.multiply(BigDecimal.valueOf(1.2));

        return price;
    }
}
