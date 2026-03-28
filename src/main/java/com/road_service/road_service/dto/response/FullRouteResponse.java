package com.road_service.road_service.dto.response;

import com.road_service.road_service.dto.request.RouteOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullRouteResponse {

    private String origin;
    private String destination;
    private List<RouteOption> routeOptions;
    private TravelAdviceDto aiTravelAdvice;
}
