package com.road_service.road_service.dto.dto;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteSegmentDTO {

    private String fromCity;
    private String fromCountry;
    private String toCity;
    private String toCountry;
    private String transportType;
    private double distanceKm;
    private double durationHours;
    private double co2EmissionKg;



}
