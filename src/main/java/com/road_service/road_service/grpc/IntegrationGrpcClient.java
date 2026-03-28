package com.road_service.road_service.grpc;

import com.road_service.road_service.dto.request.FullRouteRequest;
import com.road_service.road_service.dto.response.TravelAdviceDto;
import com.road_service.road_service.mapper.RouteMapper;
import com.travel.grpc.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class IntegrationGrpcClient {

    @GrpcClient("integration-client")
    private IntegrationGrpcServiceGrpc.IntegrationGrpcServiceBlockingStub grpcStub;
    private final RouteMapper routeMapper;

    public Map<String, Object> fetchEcoReportFromGrpc(FullRouteRequest routeRequest) {

        GrpcRouteRequest request = GrpcRouteRequest.newBuilder()
                .addAllSegments(routeMapper.toGrpcSegments(routeRequest.getSegments()))
                .build();
        GrpcEcoResponse response = grpcStub.calculateEcoFootprint(request);

        return Map.of(
                "total_co2_kg", response.getTotalCo2Kg(),
                "segment_details", response.getSegmentDetailsList(),
                "segment_co2_values", response.getSegmentCo2ValuesList()
        );
    }
    public TravelAdviceDto fetchTravelAdvice(String city, String country) {
        GrpcAdviceRequest request = GrpcAdviceRequest.newBuilder()
                .setDestinationCity(city)
                .setDestinationCountry(country)
                .build();

        GrpcAdviceResponse response = grpcStub.getTravelAdvice(request);

        return new TravelAdviceDto(
                response.getPackingListList(),
                response.getSafetyTipsList(),
                response.getWeatherAdvice(),
                response.getEmergencyContacts()
        );
    }
}