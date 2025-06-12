package com.eatclub.challenge.service.lib;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eatclub.challenge.model.restaurants.RestaurantData;

@Slf4j
@Service
public class RestaurantDataService {

    private final RestTemplate restTemplate;
    private final String dealsApiUrl;

    public RestaurantDataService(RestTemplate restTemplate,
                                @Value("${deals.api.url}") String dealsApiUrl) {
        this.restTemplate = restTemplate;
        this.dealsApiUrl = dealsApiUrl;
    }

    public RestaurantData getRestaurantData() {
        try {
            RestaurantData data = restTemplate.getForObject(dealsApiUrl, RestaurantData.class);
            if (data == null || data.getRestaurants() == null) {
                log.error("Failed to fetch restaurant data or data is null");
                throw new RuntimeException("Failed to fetch restaurant data");
            }
            return data;
        } catch (Exception e) {
            log.error("Error fetching restaurant data: {}", e.getMessage());
            throw new RuntimeException("Internal server error");
        }
    }
} 