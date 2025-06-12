package com.eatclub.challenge.service;

import com.eatclub.challenge.model.PeakTimeResponse;
import com.eatclub.challenge.model.deals.RestaurentDeals;
import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.service.lib.RestaurantDataService;

import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeakTimeService {

    private final RestaurantDataService restaurantDataService;

    public PeakTimeService(RestaurantDataService restaurantDataService) {
        this.restaurantDataService = restaurantDataService;
    }

    public PeakTimeResponse getPeakTime(String timeOfDay) {
        try {
            // Parse the input time
            LocalTime targetTime = LocalTime.parse(timeOfDay, DateTimeFormatter.ofPattern("HH:mm"));
            
            // Fetch restaurant data
            RestaurantData restaurentData = restaurantDataService.getRestaurantData();
            if (restaurentData == null || restaurentData.getRestaurants() == null) {
                log.error("Failed to fetch restaurant data or data is null");
                throw new RuntimeException("Failed to fetch restaurant data");
            }

            // Get all deals from all restaurants
            List<RestaurentDeals> allDeals = restaurentData.getRestaurants().stream()
                .flatMap(restaurant -> restaurant.getDeals().stream())
                .collect(Collectors.toList());

            // Find the deal with the most overlap with the target time
            RestaurentDeals peakDeal = findPeakDeal(allDeals, targetTime);
            
            if (peakDeal == null) {
                log.warn("No active deals found for time: {}", timeOfDay);
                return new PeakTimeResponse();
            }

            return new PeakTimeResponse(peakDeal.getOpen(), peakDeal.getClose());
        } catch (Exception e) {
            log.error("Error processing peak time: {}", e.getMessage());
            throw new RuntimeException("Internal server error");
        }
    }

    private RestaurentDeals findPeakDeal(List<RestaurentDeals> deals, LocalTime targetTime) {
        return deals.stream()
            .filter(deal -> isDealActive(deal, targetTime))
            .max((deal1, deal2) -> {
                int overlap1 = calculateOverlap(deal1, targetTime);
                int overlap2 = calculateOverlap(deal2, targetTime);
                return Integer.compare(overlap1, overlap2);
            })
            .orElse(null);
    }

    private boolean isDealActive(RestaurentDeals deal, LocalTime targetTime) {
        try {
            LocalTime openTime = LocalTime.parse(deal.getOpen().toLowerCase(), DateTimeFormatter.ofPattern("h:mma"));
            LocalTime closeTime = LocalTime.parse(deal.getClose().toLowerCase(), DateTimeFormatter.ofPattern("h:mma"));
            
            return !targetTime.isBefore(openTime) && !targetTime.isAfter(closeTime);
        } catch (Exception e) {
            log.warn("Error parsing time for deal: {}", deal.getObjectId(), e);
            return false;
        }
    }

    private int calculateOverlap(RestaurentDeals deal, LocalTime targetTime) {
        try {
            LocalTime openTime = LocalTime.parse(deal.getOpen().toLowerCase(), DateTimeFormatter.ofPattern("h:mma"));
            LocalTime closeTime = LocalTime.parse(deal.getClose().toLowerCase(), DateTimeFormatter.ofPattern("h:mma"));
            
            if (targetTime.isBefore(openTime) || targetTime.isAfter(closeTime)) {
                return 0;
            }
            
            return 1; // For simplicity, we're just checking if the time is within the deal period
        } catch (Exception e) {
            log.warn("Error calculating overlap for deal: {}", deal.getObjectId(), e);
            return 0;
        }
    }
} 