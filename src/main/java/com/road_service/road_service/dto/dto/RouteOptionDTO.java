package com.road_service.road_service.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteOptionDTO {

    private String routeName;
    private double totalDistanceKm;
    private double totalDurationHours;
    private double totalCo2Kg;
    private List<RouteSegmentDTO> segments;
    private boolean available;
    private String errorMessage;

    public static RouteOptionDTO available(String routeName, double totalDistanceKm, double totalDurationHours, double totalCo2Kg, List<RouteSegmentDTO> segments){
        return new RouteOptionDTO(routeName, 0.0, 0.0, 0.0, new ArrayList<>(), true, null);
    }
    public static RouteOptionDTO unavailable(String routeName, String errorMessage) {
        return new RouteOptionDTO(routeName, 0.0, 0.0, 0.0, new ArrayList<>(), false, errorMessage);
    }
}
