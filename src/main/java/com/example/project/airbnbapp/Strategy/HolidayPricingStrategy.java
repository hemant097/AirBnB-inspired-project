package com.example.project.airbnbapp.Strategy;

import com.example.project.airbnbapp.Entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        LocalDate inventoryDate = inventory.getDate();
        boolean isChristmasTime = isChristmasWeek(inventoryDate);
        boolean isWeekend = isWeekEnd(inventoryDate);

        if(isChristmasTime || isWeekend)
            price=price.multiply(BigDecimal.valueOf(1.25));

        return price;
    }

    //returns whether a date lies between Christmas week 24 DEC - 31 Dec
    boolean isChristmasWeek(LocalDate date){
        LocalDate start = LocalDate.of(date.getYear(), 12, 24);
        LocalDate end = LocalDate.of(date.getYear(), 12, 31).plusDays(1);

        return !date.isBefore(start) && date.isBefore(end);
    }

//    returns whether a date is among FRI,SAT,SUN
    boolean isWeekEnd(LocalDate date){
        DayOfWeek dayOfWeek =  date.getDayOfWeek();

        return (dayOfWeek== DayOfWeek.FRIDAY ||  dayOfWeek== DayOfWeek.SATURDAY || dayOfWeek==DayOfWeek.SUNDAY);
    }
}
