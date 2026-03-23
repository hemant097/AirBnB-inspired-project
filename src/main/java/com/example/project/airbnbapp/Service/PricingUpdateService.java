package com.example.project.airbnbapp.Service;

import com.example.project.airbnbapp.Entity.Hotel;
import com.example.project.airbnbapp.Entity.HotelMinPrice;
import com.example.project.airbnbapp.Entity.Inventory;
import com.example.project.airbnbapp.Repository.HotelMinPriceRepository;
import com.example.project.airbnbapp.Repository.HotelRepository;
import com.example.project.airbnbapp.Repository.InventoryRepository;
import com.example.project.airbnbapp.Strategy.PricingService;
import com.example.project.airbnbapp.Strategy.PricingStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {
    private final HotelRepository hotelRepo;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices(){
        int page =0;
        int batchSize = 100;

        while(true){
            Page<Hotel> hotelPage = hotelRepo.findAll(PageRequest.of(page,batchSize));
            if(hotelPage.isEmpty()) break;

            hotelPage.getContent().forEach( hotel -> updateHotelPrices(hotel));

            page++;
        }
    }

    private void updateHotelPrices(Hotel hotel){
        log.info("updating hotel prices for hotel id : {}",hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        List<Inventory> inventoryList=inventoryRepository.findAllByHotelAndDateBetween(hotel, startDate, endDate);

        updateInventoryPrices(inventoryList);

        updateHotelMinPrice(hotel, inventoryList, startDate, endDate);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        //compare minimum price per day for the hotel

        /**Logic explanation, in this case
         * 1. Collectors.groupingBy creates a map, where key is the inventoryDate, but is stores full objects(Inventory) in the map value
         * after 1. it becomes something like
         * Jan 1 -> [inv1, inv2, inv3]
         * Jan 2 -> [inv4, inv5]
         *
         * 2. Collectors.mapping extracts the price, so instead of storing the full object in the map value, we only store price
         * after 2. it becomes something like
         * Jan 1 -> [10.5, 8.0, 12.3]
         * Jan 2 -> [7.2, 9.1]
         *
         * 3. Collectors.minBy() finds the smallest price in each group, and because minBy may have empty input, it returns Optional
         *
         * Jan 1-> Optional(8.0)
         * Jan 2-> Optional(7.2)
         *
         * 4. in this step, we are getting entrySet from the Map<LocalDate,Optional<Double> > received from step 3,
         * and creating a stream on it's entrySet, and creating a Map<LocalDate, Double>
         *     .orElse(BigDecimal.ZERO) 'll always return value inside Optional, as every K-V pair will have a value
         *          inside its Optional
         *
         *
         * */
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        inventory -> inventory.getDate(),
                        Collectors.mapping( Inventory::getPrice, Collectors.minBy(Comparator.comparing(p-> p)))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        mapEntry -> mapEntry.getKey(),
                        mapEntry -> mapEntry.getValue().orElse(BigDecimal.ZERO)));


        //Prepare hotelMinPrice entities in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach( (date, price) -> {
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel,date)
                    .orElse(new HotelMinPrice(hotel,date));
            hotelMinPrice.setPrice(price);
            hotelPrices.add(hotelMinPrice);

        });

        //save all HotelMinPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelPrices);

    }

    private void updateInventoryPrices(List<Inventory> inventoryList){

        inventoryList.forEach( inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });

        inventoryRepository.saveAll(inventoryList);

    }
}
