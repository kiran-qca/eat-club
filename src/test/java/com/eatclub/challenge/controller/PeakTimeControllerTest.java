package com.eatclub.challenge.controller;

import com.eatclub.challenge.model.PeakTimeResponse;
import com.eatclub.challenge.service.PeakTimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PeakTimeController.class)
class PeakTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PeakTimeService peakTimeService;

    @Test
    void getPeakTimeWindow_Success() throws Exception {
        // Arrange
        PeakTimeResponse mockResponse = new PeakTimeResponse();
        mockResponse.setStartTime("6:00PM");
        mockResponse.setEndTime("9:00PM");
        when(peakTimeService.getPeakTime()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/peak-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists());
    }

    @Test
    void getPeakTimeWindow_ServiceError() throws Exception {
        // Arrange
        when(peakTimeService.getPeakTime())
                .thenThrow(new RuntimeException("Internal server error"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/peak-time"))
                .andExpect(status().isInternalServerError());
    }
} 