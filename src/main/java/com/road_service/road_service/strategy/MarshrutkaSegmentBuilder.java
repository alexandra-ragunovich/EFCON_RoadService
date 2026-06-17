package com.road_service.road_service.strategy;

import com.road_service.road_service.dto.dto.RouteSegmentDTO;
import com.road_service.road_service.entity.CityEntity;
import com.road_service.road_service.service.segment.RouteSegmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarshrutkaSegmentBuilder implements SegmentBuilder {

    private final RouteSegmentService segmentService;

    @Override
    public String getTransportType() {
        return "МАРШРУТКА";
    }

    @Override
    public Optional<RouteSegmentDTO> buildSegment(CityEntity from, CityEntity to) {

        return segmentService.findSegment(from, to, "МАРШРУТКА", (f, t, type) -> Optional.empty());
    }
}