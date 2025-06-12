package com.eatclub.challenge.model.deals;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveRestaurantDeal {
    private String restaurantObjectId;
    private String restaurantName;
    private String restaurantAddress1;
    private String restaurantSuburb;
    private String restaurantOpen;
    private String restaurantClose;
    private String dealObjectId;
    private String discount;
    private String dineIn;
    private String lightning;
    private String qtyLeft;
} 