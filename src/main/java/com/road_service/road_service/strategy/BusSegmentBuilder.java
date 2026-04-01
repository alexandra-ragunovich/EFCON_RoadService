package com.road_service.road_service.strategy;

import com.road_service.road_service.dto.dto.RouteSegmentDTO;
import com.road_service.road_service.entity.CityEntity;
import com.road_service.road_service.integretion.YandexRaspisaniyaClient;
import com.road_service.road_service.service.GeoCalculator;
import com.road_service.road_service.service.segment.RouteSegmentService;
import com.road_service.road_service.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BusSegmentBuilder implements SegmentBuilder {

    private final RouteSegmentService segmentService;
    private final YandexRaspisaniyaClient yandexClient;
    private final GeoCalculator geoCalculator;

    @Override
    public String getTransportType() {
        return "АВТОБУС";
    }

    @Override
    public Optional<RouteSegmentDTO> buildSegment(CityEntity from, CityEntity to) {
        return segmentService.findSegment(from, to, "АВТОБУС", (f, t, type) -> findInYandex(f, t));
    }

    private Optional<RouteSegmentDTO> findInYandex(CityEntity from, CityEntity to) {

        double[] yandexResult = yandexClient.findRoute(from.getName(), to.getName(), "АВТОБУС");

        if (yandexResult == null || yandexResult.length == 0) {
            return Optional.empty();
        }

        double durationHours = yandexResult[0];
        double distanceKm = yandexResult[1] > 0
                ? yandexResult[1]
                : geoCalculator.distance(from, to) * 1.2;

        return Optional.of(
                new RouteSegmentDTO( from.getName(), from.getCountry(), to.getName(), to.getCountry(), "АВТОБУС", MathUtils.round(distanceKm), MathUtils.round(durationHours), 0.0
        ));
    }
}