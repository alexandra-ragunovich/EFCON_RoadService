package com.road_service.road_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteBuildRequest {

    private String fromCity;
    private String fromCountry;
    private String toCity;
    private String toCountry;
    private int waypointsCount;

}
