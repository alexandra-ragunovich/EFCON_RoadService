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
public class FlightSegmentBuilder implements SegmentBuilder {

    private final RouteSegmentService segmentService;
    private final YandexRaspisaniyaClient yandexClient;
    private final GeoCalculator geoCalculator;

    @Override
    public String getTransportType() {
        return "САМОЛЕТ";
    }

    @Override
    public Optional<RouteSegmentDTO> buildSegment(CityEntity from, CityEntity to) {
        return segmentService.findSegment(from, to, "САМОЛЕТ", (f, t, type) -> findInYandex(f, t));
    }

    private Optional<RouteSegmentDTO> findInYandex(CityEntity from, CityEntity to) {
        if (from.getIataCode() == null || to.getIataCode() == null) {
            return Optional.empty();
        }

        double[] yandexResult = yandexClient.findFlightByIata(from.getIataCode(), to.getIataCode());

        if (yandexResult == null || yandexResult.length == 0) {
            return Optional.empty();
        }

        double flyDist = geoCalculator.distance(from, to);
        double flyDur = yandexResult[0] > 0 ? yandexResult[0] : flyDist / 800.0;

        return Optional.of(
                new RouteSegmentDTO(from.getName(), from.getCountry(), to.getName(), to.getCountry(), "САМОЛЕТ", MathUtils.round(flyDist), MathUtils.round(flyDur), 0.0
        ));
    }
}