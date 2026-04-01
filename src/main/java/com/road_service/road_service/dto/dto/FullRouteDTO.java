package com.road_service.road_service.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullRouteDTO {

    private List<RouteSegmentDTO> segments;
}
