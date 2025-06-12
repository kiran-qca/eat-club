package com.eatclub.challenge.controller;

import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.service.lib.RestaurantDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RestaurantDataController {

    private final RestaurantDataService restaurantDataService;

    @GetMapping("/restaurants")
    public ResponseEntity<RestaurantData> getRestaurantData() {
        try {
            RestaurantData data = restaurantDataService.getRestaurantData();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error fetching restaurant data: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
} 