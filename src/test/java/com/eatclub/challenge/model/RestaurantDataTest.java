package com.eatclub.challenge.model;

import org.junit.jupiter.api.Test;

import com.eatclub.challenge.model.restaurants.RestaurantData;
import com.eatclub.challenge.model.restaurants.RestaurantDetail;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RestaurantDataTest {

    @Test
    void testRestaurentData() {
        // Create test data
        RestaurantDetail restaurant1 = new RestaurantDetail();
        restaurant1.setObjectId("REST1");
        restaurant1.setName("Restaurant 1");
        restaurant1.setAddress1("123 Main St");
        restaurant1.setSuburb("Downtown");

        RestaurantDetail restaurant2 = new RestaurantDetail();
        restaurant2.setObjectId("REST2");
        restaurant2.setName("Restaurant 2");
        restaurant2.setAddress1("456 Oak St");
        restaurant2.setSuburb("Uptown");

        List<RestaurantDetail> restaurants = Arrays.asList(restaurant1, restaurant2);

        // Create RestaurentData instance
        RestaurantData restaurentData = new RestaurantData();
        restaurentData.setRestaurants(restaurants);

        // Verify
        assertNotNull(restaurentData.getRestaurants());
        assertEquals(2, restaurentData.getRestaurants().size());
        assertEquals("REST1", restaurentData.getRestaurants().get(0).getObjectId());
        assertEquals("Restaurant 1", restaurentData.getRestaurants().get(0).getName());
        assertEquals("REST2", restaurentData.getRestaurants().get(1).getObjectId());
        assertEquals("Restaurant 2", restaurentData.getRestaurants().get(1).getName());
    }

    @Test
    void testRestaurentDataWithNullRestaurants() {
        RestaurantData restaurentData = new RestaurantData();
        restaurentData.setRestaurants(null);
        
        assertNull(restaurentData.getRestaurants());
    }

    @Test
    void testRestaurentDataWithEmptyRestaurants() {
        RestaurantData restaurentData = new RestaurantData();
        restaurentData.setRestaurants(List.of());
        
        assertNotNull(restaurentData.getRestaurants());
        assertTrue(restaurentData.getRestaurants().isEmpty());
    }
} 