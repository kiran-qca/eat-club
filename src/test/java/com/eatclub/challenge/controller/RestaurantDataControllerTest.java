package com.eatclub.challenge.controller;

import com.eatclub.challenge.model.deals.RestaurentDeals;
import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.model.restaurants.RestaurantDetail;
import com.eatclub.challenge.service.lib.RestaurantDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantDataController.class)
class RestaurantDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantDataService restaurantDataService;

    @Test
    void getRestaurantData_Success() throws Exception {
        // Arrange
        RestaurantData mockData = createMockRestaurantData();
        when(restaurantDataService.getRestaurantData()).thenReturn(mockData);

        // Act & Assert
        mockMvc.perform(get("/api/v1/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants").exists())
                .andExpect(jsonPath("$.restaurants[0].name").value("Test Restaurant"))
                .andExpect(jsonPath("$.restaurants[0].deals[0].open").value("6:00PM"))
                .andExpect(jsonPath("$.restaurants[0].deals[0].close").value("9:00PM"));
    }

    @Test
    void getRestaurantData_ServiceError() throws Exception {
        // Arrange
        when(restaurantDataService.getRestaurantData()).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/restaurants"))
                .andExpect(status().isInternalServerError());
    }

    private RestaurantData createMockRestaurantData() {
        RestaurantData data = new RestaurantData();
        List<RestaurantDetail> restaurants = new ArrayList<>();
        RestaurantDetail restaurant = new RestaurantDetail();
        restaurant.setName("Test Restaurant");
        restaurant.setDeals(new ArrayList<>());
        RestaurentDeals deal = new RestaurentDeals();
        deal.setOpen("6:00PM");
        deal.setClose("9:00PM");
        restaurant.getDeals().add(deal);
        restaurants.add(restaurant);
        data.setRestaurants(restaurants);
        return data;
    }
} 