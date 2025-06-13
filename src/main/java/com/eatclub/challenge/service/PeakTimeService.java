package com.eatclub.challenge.service;

import com.eatclub.challenge.model.PeakTimeResponse;
import com.eatclub.challenge.model.deals.RestaurentDeals;
import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.service.lib.RestaurantDataService;

import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeakTimeService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma");
    private static final DateTimeFormatter OUTPUT_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma");

    private final RestaurantDataService restaurantDataService;

    public PeakTimeService(RestaurantDataService restaurantDataService) {
        this.restaurantDataService = restaurantDataService;
    }

    public PeakTimeResponse getPeakTime() {
        try {
            // Fetch restaurant data
            RestaurantData restaurantData = Optional.ofNullable(restaurantDataService.getRestaurantData())
                    .orElseThrow(() -> new RuntimeException("Failed to fetch restaurant data"));

            // Get all deals from all restaurants
            List<RestaurentDeals> allDeals = Optional.ofNullable(restaurantData.getRestaurants())
                    .orElse(List.of())
                    .stream()
                    .flatMap(restaurant -> Optional.ofNullable(restaurant.getDeals())
                            .orElse(List.of())
                            .stream())
                    .collect(Collectors.toList());

            if (allDeals.isEmpty()) {
                log.warn("No deals found");
                return new PeakTimeResponse();
            }

            // Find the time window with most active deals
            Map<LocalTime, Integer> hourlyDealCount = new HashMap<>();
            
            // Count deals for each hour
            for (RestaurentDeals deal : allDeals) {
                try {
                    String openTimeStr = Optional.ofNullable(deal.getOpen())
                            .map(String::toLowerCase)
                            .orElse("12:00am");
                    String closeTimeStr = Optional.ofNullable(deal.getClose())
                            .map(String::toLowerCase)
                            .orElse("11:59pm");

                    LocalTime openTime = LocalTime.parse(openTimeStr, TIME_FORMATTER);
                    LocalTime closeTime = LocalTime.parse(closeTimeStr, TIME_FORMATTER);

                    // Round times to nearest hour
                    openTime = roundToNearestHour(openTime);
                    closeTime = roundToNearestHour(closeTime);

                    // Handle overnight deals
                    if (closeTime.isBefore(openTime)) {
                        // Count deals for hours from open time to midnight
                        for (LocalTime time = openTime; !time.equals(LocalTime.MIDNIGHT); time = time.plusHours(1)) {
                            hourlyDealCount.merge(time, 1, Integer::sum);
                        }
                        // Count deals for hours from midnight to close time
                        for (LocalTime time = LocalTime.MIDNIGHT; !time.equals(closeTime); time = time.plusHours(1)) {
                            hourlyDealCount.merge(time, 1, Integer::sum);
                        }
                    } else {
                        // Count deals for each hour in the time window
                        for (LocalTime time = openTime; !time.equals(closeTime); time = time.plusHours(1)) {
                            hourlyDealCount.merge(time, 1, Integer::sum);
                        }
                    }
                } catch (DateTimeParseException e) {
                    log.warn("Error parsing time for deal: {}", deal.getObjectId());
                }
            }

            // Find the hour with maximum deals
            Optional<Map.Entry<LocalTime, Integer>> maxDeals = hourlyDealCount.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());

            if (maxDeals.isEmpty()) {
                return new PeakTimeResponse();
            }

            LocalTime peakHour = maxDeals.get().getKey();
            String startTime = peakHour.format(OUTPUT_TIME_FORMATTER);
            String endTime = peakHour.plusHours(1).format(OUTPUT_TIME_FORMATTER);

            return new PeakTimeResponse(startTime, endTime);
        } catch (Exception e) {
            log.error("Error processing peak time: {}", e.getMessage());
            throw new RuntimeException("Internal server error");
        }
    }

    private LocalTime roundToNearestHour(LocalTime time) {
        int minutes = time.getMinute();
        if (minutes >= 30) {
            return time.plusHours(1).withMinute(0);
        }
        return time.withMinute(0);
    }
} 