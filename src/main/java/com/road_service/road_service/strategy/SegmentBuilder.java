package com.road_service.road_service.strategy;

import com.road_service.road_service.dto.dto.RouteSegmentDTO;
import com.road_service.road_service.entity.CityEntity;

import java.util.Optional;

public interface SegmentBuilder {
    Optional<RouteSegmentDTO> buildSegment(CityEntity from, CityEntity to);
    String getTransportType();
}
