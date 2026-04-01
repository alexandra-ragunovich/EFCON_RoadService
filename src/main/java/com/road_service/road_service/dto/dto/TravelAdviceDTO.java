package com.road_service.road_service.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelAdviceDTO {
    private List<String> packingList;
    private List<String> safetyTips;
    private String weatherAdvice;
    private String emergencyContacts;
}