package com.eatclub.challenge.controller;

import com.eatclub.challenge.model.deals.DealsResponse;
import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.service.DealService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deals")
public class RestaurantDealController {

    private final DealService dealService;

    public RestaurantDealController(DealService dealService) {
        this.dealService = dealService;
    }

    @GetMapping
    public ResponseEntity<DealsResponse> getActiveDeals(@RequestParam String timeOfDay) {
        try {
            DealsResponse activeDeals = dealService.getActiveDeals(timeOfDay);
            return ResponseEntity.ok(activeDeals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 