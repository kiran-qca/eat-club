package com.eatclub.challenge.model.restaurants;

import java.util.List;

import com.eatclub.challenge.model.deals.RestaurentDeals;

import lombok.Data;

@Data
public class RestaurantDetail {
    private String objectId;
    private String name;
    private String address1;
    private String suburb;
    private List<String> cuisines;
    private String imageLink;
    private String open;
    private String close;
    private List<RestaurentDeals> deals;
}
