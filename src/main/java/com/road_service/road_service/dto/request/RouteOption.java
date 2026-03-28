package com.road_service.road_service.dto.request;

import com.road_service.road_service.dto.response.TravelAdviceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteOption {

    private String routeName;
    private double totalDistanceKm;
    private double totalDurationHours;
    private double totalCo2Kg;
    private List<RouteSegmentRequest> segments;
    private boolean available;
    private String errorMessage;


    public RouteOption(String routeName, double totalDistanceKm, double totalDurationHours, double totalCo2Kg, List<RouteSegmentRequest> segments) {
        this.routeName = routeName;
        this.totalDistanceKm = totalDistanceKm;
        this.totalDurationHours = totalDurationHours;
        this.totalCo2Kg = totalCo2Kg;
        this.segments = segments;
        this.available = true;
        this.errorMessage = null;
    }
    public static RouteOption unavailable(String routeName, String errorMessage) {
        return new RouteOption(routeName, 0.0, 0.0, 0.0, new ArrayList<>(), false, errorMessage);
    }
}
