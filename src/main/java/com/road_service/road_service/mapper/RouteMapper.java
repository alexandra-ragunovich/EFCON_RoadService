package com.road_service.road_service.mapper;

import com.road_service.road_service.dto.request.RouteSegmentRequest;
import com.travel.grpc.GrpcRouteSegment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RouteMapper {

    public GrpcRouteSegment toGrpcSegment(RouteSegmentRequest segment) {
        return GrpcRouteSegment.newBuilder()
                .setFromCity(segment.getFromCity())
                .setToCity(segment.getToCity())
                .setToCountry(segment.getToCountry() != null ? segment.getToCountry() : "Unknown")
                .setTransportType(segment.getTransportType())
                .setDistanceKm(segment.getDistanceKm())
                .build();
    }


    public List<GrpcRouteSegment> toGrpcSegments(List<RouteSegmentRequest> segments) {
        return segments.stream()
                .map(this::toGrpcSegment)
                .collect(Collectors.toList());
    }
}
