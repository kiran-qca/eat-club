package com.eatclub.challenge.model.deals;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DealsResponse {
    private List<LiveRestaurantDeal> liveDeals;
}  