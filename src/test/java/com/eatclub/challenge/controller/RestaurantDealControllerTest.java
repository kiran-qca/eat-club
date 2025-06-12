package com.eatclub.challenge.controller;

import com.eatclub.challenge.model.deals.DealsResponse;
import com.eatclub.challenge.model.deals.LiveRestaurantDeal;
import com.eatclub.challenge.model.deals.RestaurentDeals;
import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.model.restaurants.RestaurantDetail;
import com.eatclub.challenge.service.DealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantDealController.class)
class RestaurantDealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealService dealService;

    @Test
    void getActiveDeals_Success() throws Exception {
        // Arrange
        DealsResponse mockResponse = createMockDealsResponse();
        when(dealService.getActiveDeals(anyString())).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/deals")
                .param("timeOfDay", "6:00PM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liveDeals").exists())
                .andExpect(jsonPath("$.liveDeals[0].restaurantName").value("Test Restaurant"))
                .andExpect(jsonPath("$.liveDeals[0].dealObjectId").value("DEAL1"));
    }

    @Test
    void getActiveDeals_InvalidTimeFormat() throws Exception {
        // Arrange
        when(dealService.getActiveDeals("invalid-time")).thenThrow(new IllegalArgumentException("Invalid time format"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/deals")
                .param("timeOfDay", "invalid-time"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getActiveDeals_ServiceError() throws Exception {
        // Arrange
        when(dealService.getActiveDeals(anyString())).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/deals")
                .param("timeOfDay", "6:00PM"))
                .andExpect(status().isInternalServerError());
    }

    private DealsResponse createMockDealsResponse() {
        List<LiveRestaurantDeal> liveDeals = new ArrayList<>();
        LiveRestaurantDeal deal = LiveRestaurantDeal.builder()
                .restaurantObjectId("REST1")
                .restaurantName("Test Restaurant")
                .restaurantAddress1("123 Test St")
                .restaurantSuburb("Test Suburb")
                .restaurantOpen("6:00PM")
                .restaurantClose("9:00PM")
                .dealObjectId("DEAL1")
                .discount("20%")
                .dineIn("true")
                .lightning("false")
                .qtyLeft("5")
                .build();
        liveDeals.add(deal);
        return DealsResponse.builder().liveDeals(liveDeals).build();
    }
} 