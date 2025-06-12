package com.eatclub.challenge.service;

import com.eatclub.challenge.model.deals.DealsResponse;
import com.eatclub.challenge.model.deals.LiveRestaurantDeal;
import com.eatclub.challenge.model.deals.RestaurentDeals;
import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.model.restaurants.RestaurantDetail;
import com.eatclub.challenge.service.lib.RestaurantDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DealService {

    private static final Logger logger = LoggerFactory.getLogger(DealService.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma");
    private static final DateTimeFormatter INPUT_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma");

    private final RestaurantDataService restaurantDataService;

    @Value("${deals.api.url}")
    private String dealsApiUrl;

    public DealService(RestaurantDataService restaurantDataService) {
        this.restaurantDataService = restaurantDataService;
    }

    public DealsResponse getActiveDeals(String timeOfDay) {
        return Optional.ofNullable(timeOfDay)
                .map(this::parseTime)
                .map(this::fetchAndProcessDeals)
                .orElseThrow(() -> new IllegalArgumentException("Invalid time format. Please use HH:mm format (e.g., 18:00)"));
    }

    private LocalTime parseTime(String timeOfDay) {
        try {
            return LocalTime.parse(timeOfDay, INPUT_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing time: {}", timeOfDay, e);
            throw new IllegalArgumentException("Invalid time format. Please use HH:mm format (e.g., 18:00)");
        }
    }

    private DealsResponse fetchAndProcessDeals(LocalTime currentTime) {
        try {
            RestaurantData restaurantData = restaurantDataService.getRestaurantData();

            List<LiveRestaurantDeal> activeDeals = Optional.ofNullable(restaurantData.getRestaurants())
                    .orElse(List.of())
                    .stream()
                    .flatMap(restaurant -> filterActiveDeals(restaurant.getDeals(), currentTime)
                            .stream()
                            .map(deal -> createLiveDeal(restaurant, deal)))
                    .collect(Collectors.toList());

            return createDealResponse(activeDeals);
        } catch (Exception e) {
            logger.error("Error processing deals: {}", e.getMessage(), e);
            throw new RuntimeException("Internal server error");
        }
    }

    private LiveRestaurantDeal createLiveDeal(RestaurantDetail restaurant, RestaurentDeals deal) {
        return LiveRestaurantDeal.builder()
                .restaurantObjectId(restaurant.getObjectId())
                .restaurantName(restaurant.getName())
                .restaurantAddress1(restaurant.getAddress1())
                .restaurantSuburb(restaurant.getSuburb())
                .restaurantOpen(restaurant.getOpen())
                .restaurantClose(restaurant.getClose())
                .dealObjectId(deal.getObjectId())
                .discount(deal.getDiscount())
                .dineIn(deal.getDineIn())
                .lightning(deal.getLightning())
                .qtyLeft(deal.getQtyLeft())
                .build();
    }

    private DealsResponse createDealResponse(List<LiveRestaurantDeal> deals) {
        return DealsResponse.builder().liveDeals(deals).build();
    }

    private List<RestaurentDeals> filterActiveDeals(List<RestaurentDeals> deals, LocalTime currentTime) {
        return Optional.ofNullable(deals)
                .orElse(List.of())
                .stream()
                .filter(deal -> isDealActive(deal, currentTime))
                .collect(Collectors.toList());
    }

    private boolean isDealActive(RestaurentDeals deal, LocalTime currentTime) {
        try {
            if(deal.getOpen() == null || deal.getClose() == null) {
                return false;
            }

            LocalTime openTime = LocalTime.parse(deal.getOpen().toLowerCase(), TIME_FORMATTER);
            LocalTime closeTime = LocalTime.parse(deal.getClose().toLowerCase(), TIME_FORMATTER);

            // Handle cases where close time is on the next day (e.g., 11:59pm)
            if (closeTime.isBefore(openTime)) {
                return currentTime.isAfter(openTime) || currentTime.isBefore(closeTime);
            }

            // Normal case where times are on the same day
            return currentTime.isAfter(openTime) && currentTime.isBefore(closeTime);
        } catch (DateTimeParseException e) {
            logger.warn("Error parsing time for deal: {}", deal.getObjectId());
            return false;
        }
    }
}