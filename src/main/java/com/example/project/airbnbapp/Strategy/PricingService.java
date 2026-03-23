package com.example.project.airbnbapp.Strategy;

import com.example.project.airbnbapp.Entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory){
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        //applying additonal strategies in decorator design pattern
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        return  pricingStrategy.calculatePrice(inventory);

    }

    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList){
        //returns the total amount of all the days
        return inventoryList.stream()
                .map( inv -> calculateDynamicPricing(inv))
                .reduce(BigDecimal.ZERO, (price1,price2) -> price1.add(price2));

        //return the max amount for all the days (due to each day having different price)
//        return inventoryList.stream()
//                .map(this::calculateDynamicPricing)
//                .max( (num1,num2) -> num1.compareTo(num2))
//                .orElse(BigDecimal.ZERO);
    }
}