package com.eatclub.challenge.service;

import com.eatclub.challenge.model.PeakTimeResponse;
import com.eatclub.challenge.model.deals.RestaurentDeals;
import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.model.restaurants.RestaurantDetail;
import com.eatclub.challenge.service.lib.RestaurantDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeakTimeServiceTest {

    @Mock
    private RestaurantDataService restaurantDataService;

    @InjectMocks
    private PeakTimeService peakTimeService;

    @Test
    void getPeakTime_Success() {
        // Arrange
        RestaurantData mockData = createMockRestaurantData();
        when(restaurantDataService.getRestaurantData()).thenReturn(mockData);

        // Act
        PeakTimeResponse response = peakTimeService.getPeakTime();

        // Assert
        assertNotNull(response);
        assertEquals("6:00pm", response.getStartTime());
        assertEquals("7:00pm", response.getEndTime());
        verify(restaurantDataService).getRestaurantData();
    }

    @Test
    void getPeakTime_EmptyRestaurants() {
        // Arrange
        RestaurantData emptyData = new RestaurantData();
        emptyData.setRestaurants(new ArrayList<>());
        when(restaurantDataService.getRestaurantData()).thenReturn(emptyData);

        // Act
        PeakTimeResponse response = peakTimeService.getPeakTime();

        // Assert
        assertNotNull(response);
        assertNull(response.getStartTime());
        assertNull(response.getEndTime());
        verify(restaurantDataService).getRestaurantData();
    }


    @Test
    void getPeakTime_ServiceError() {
        // Arrange
        when(restaurantDataService.getRestaurantData())
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> peakTimeService.getPeakTime());
        verify(restaurantDataService).getRestaurantData();
    }

    private RestaurantData createMockRestaurantData() {
        RestaurantData data = new RestaurantData();
        data.setRestaurants(new ArrayList<>());
        RestaurantDetail restaurant = new RestaurantDetail();
        restaurant.setDeals(new ArrayList<>());
        RestaurentDeals deal = new RestaurentDeals();
        deal.setOpen("6:00pm");
        deal.setClose("7:00pm");
        restaurant.getDeals().add(deal);
        data.getRestaurants().add(restaurant);
        return data;
    }
} 