package com.road_service.road_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelAdviceDto {
    private List<String> packingList;
    private List<String> safetyTips;
    private String weatherAdvice;
    private String emergencyContacts;
}