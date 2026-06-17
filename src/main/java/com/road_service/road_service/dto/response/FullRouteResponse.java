package com.road_service.road_service.dto.response;

import com.road_service.road_service.dto.dto.RouteOptionDTO;
import com.road_service.road_service.dto.dto.TravelAdviceDTO;
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
    private List<RouteOptionDTO> routeOptions;
    private TravelAdviceDTO aiTravelAdvice;
}
