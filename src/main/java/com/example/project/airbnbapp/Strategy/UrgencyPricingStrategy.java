package com.example.project.airbnbapp.Strategy;

import com.example.project.airbnbapp.Entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {
    private final PricingStrategy wrapped;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {

        BigDecimal price = wrapped.calculatePrice(inventory);

        LocalDate inventoryDate = inventory.getDate();
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAfter = today.plusDays(7);

//       checking whether inventoryDate is between today (inclusive) and one week from today (exclusive).
        if(!inventoryDate.isBefore(today) && inventoryDate.isBefore(oneWeekAfter))
            price = price.multiply(BigDecimal.valueOf(1.15));

        return price;

        //inventoryDate.isBefore(today) means the date which is before today, now putting ! on this reverses
        // it to all the dates which are not this, i.e., inventoryDate >= today , so condition on the left of && checks
        // whether a date is today or after today.
        //And condition on the right of && checks, whether the date is within one week, not more than that.
    }
}
