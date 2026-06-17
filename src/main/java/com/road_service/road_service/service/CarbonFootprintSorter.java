package com.road_service.road_service.service;

import com.road_service.road_service.dto.dto.FullRouteDTO;
import com.road_service.road_service.dto.dto.RouteOptionDTO;
import com.road_service.road_service.grpc.IntegrationGrpcClient;
import com.road_service.road_service.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CarbonFootprintSorter {

    private final IntegrationGrpcClient carbonGrpcClient;

    public void processAndSort(List<RouteOptionDTO> options) {

        for (RouteOptionDTO option : options) {
            calculateCo2(option);
        }

        options.sort(Comparator.comparingDouble(RouteOptionDTO::getTotalCo2Kg));
    }

    private void calculateCo2(RouteOptionDTO option) {

        try {
            FullRouteDTO request = new FullRouteDTO(option.getSegments());
            var ecoReport = carbonGrpcClient.fetchEcoReportFromGrpc(request);

            double totalCo2 = (double) ecoReport.get("total_co2_kg");
            option.setTotalCo2Kg(MathUtils.round(totalCo2));

            @SuppressWarnings("unchecked")
            List<Double> co2Values = (List<Double>) ecoReport.get("segment_co2_values");
            if (co2Values != null) {
                for (int i = 0; i < option.getSegments().size() && i < co2Values.size(); i++) {
                    option.getSegments().get(i).setCo2EmissionKg(MathUtils.round(co2Values.get(i)));
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка gRPC: " + e.getMessage());
            option.setTotalCo2Kg(9999.9);
        }
    }
}