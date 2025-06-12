package com.eatclub.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeakTimeResponse {
    private String startTime;
    private String endTime;
} 