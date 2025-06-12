package com.eatclub.challenge.model.deals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurentDeals {
    private String objectId;
    private String discount;
    private String dineIn;
    private String lightning;
    private String open;
    private String close;
    private String qtyLeft;
}
