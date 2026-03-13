package com.example.project.airbnbapp.Strategy;

import com.example.project.airbnbapp.Entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

//Decorator design pattern
public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

    @Service
    class PricingService {

        public BigDecimal calculateDynamicPricing(Inventory inventory){
            PricingStrategy pricingStrategy = new BasePricingStrategy();

            //applying additonal strategies in decorator design pattern
            pricingStrategy = new SurgePricingStrategy(pricingStrategy);
            pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
            pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
            pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

            return  pricingStrategy.calculatePrice(inventory);

        }
    }
}
