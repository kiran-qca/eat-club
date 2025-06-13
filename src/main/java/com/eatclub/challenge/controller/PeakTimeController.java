package com.eatclub.challenge.controller;

import com.eatclub.challenge.model.PeakTimeResponse;
import com.eatclub.challenge.service.PeakTimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PeakTimeController {

    private final PeakTimeService peakTimeService;

    @GetMapping("/peak-time")
    public PeakTimeResponse getPeakTimeWindow() {
        return peakTimeService.getPeakTime();
    }
} 