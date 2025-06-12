package com.eatclub.challenge.service;

import com.eatclub.challenge.model.deals.DealsResponse;
import com.eatclub.challenge.model.deals.RestaurentDeals;
import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.model.restaurants.RestaurantDetail;
import com.eatclub.challenge.service.lib.RestaurantDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private RestaurantDataService restaurantDataService;

    @InjectMocks
    private DealService dealService;

    private static final String TEST_API_URL = "http://test-api-url";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dealService, "dealsApiUrl", TEST_API_URL);
    }

    @Test
    void getActiveDeals_Success() {
        // Arrange
        RestaurantData mockData = createMockRestaurantData();
        when(restaurantDataService.getRestaurantData()).thenReturn(mockData);

        // Act
        DealsResponse result = dealService.getActiveDeals("6:00PM");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLiveDeals());
        assertEquals(1, result.getLiveDeals().size());
        verify(restaurantDataService).getRestaurantData();
    }

    @Test
    void getActiveDeals_WhenInvalidTimeFormat_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> dealService.getActiveDeals("invalid-time"));
    }

    @Test
    void getActiveDeals_WhenServiceError_ThrowsException() {
        // Arrange
        when(restaurantDataService.getRestaurantData())
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> dealService.getActiveDeals("6:00PM"));
        verify(restaurantDataService).getRestaurantData();
    }

    @Test
    void getActiveDeals_WhenDealTimesAreNull_ReturnsEmptyList() {
        // Arrange
        RestaurantData mockData = createMockRestaurantDataWithNullTimes();
        when(restaurantDataService.getRestaurantData()).thenReturn(mockData);

        // Act
        DealsResponse result = dealService.getActiveDeals("6:00PM");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLiveDeals());
        assertTrue(result.getLiveDeals().isEmpty());
        verify(restaurantDataService).getRestaurantData();
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

    private RestaurantData createMockRestaurantDataWithNullTimes() {
        RestaurantData data = new RestaurantData();
        List<RestaurantDetail> restaurants = new ArrayList<>();
        RestaurantDetail restaurant = new RestaurantDetail();
        restaurant.setName("Test Restaurant");
        restaurant.setDeals(new ArrayList<>());
        RestaurentDeals deal = new RestaurentDeals();
        deal.setOpen(null);
        deal.setClose(null);
        restaurant.getDeals().add(deal);
        restaurants.add(restaurant);
        data.setRestaurants(restaurants);
        return data;
    }
} 