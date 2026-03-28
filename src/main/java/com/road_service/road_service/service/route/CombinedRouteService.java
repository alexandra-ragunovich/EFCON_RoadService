package com.road_service.road_service.service.route;

import com.road_service.road_service.dto.request.RouteOption;
import com.road_service.road_service.dto.request.RouteSegmentRequest;
import com.road_service.road_service.entity.CityEntity;
import com.road_service.road_service.strategy.*;
import com.road_service.road_service.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CombinedRouteService {

    private final TrainSegmentBuilder trainBuilder;
    private final SuburbanSegmentBuilder suburbanBuilder;
    private final BusSegmentBuilder busBuilder;
    private final MarshrutkaSegmentBuilder marshrutkaBuilder;
    private final FlightSegmentBuilder flightBuilder;

    private List<SegmentBuilder> getAllBuilders() {
        return List.of(trainBuilder, suburbanBuilder, busBuilder, marshrutkaBuilder, flightBuilder);
    }

    public RouteOption buildMixedRoute(List<CityEntity> chain) {

        if (chain.size() < 3) {
            return RouteOption.unavailable("Смешанный",
                    "Смешанный маршрут возможен только при наличии промежуточных остановок");
        }

        List<RouteSegmentRequest> segments = new ArrayList<>();
        double totalDist = 0;
        double totalDur = 0;

        Set<String> transportTypes = new HashSet<>();

        for (int i = 0; i < chain.size() - 1; i++) {

            CityEntity from = chain.get(i);
            CityEntity to = chain.get(i + 1);

            Optional<RouteSegmentRequest> segment = Optional.empty();

            for (SegmentBuilder builder : getAllBuilders()) {
                segment = builder.buildSegment(from, to);
                if (segment.isPresent()) break;
            }

            if (segment.isEmpty()) {
                return RouteOption.unavailable("Смешанный",
                        "Нет доступного транспорта между г. " + from.getName() + " и г. " + to.getName());
            }

            segments.add(segment.get());
            transportTypes.add(segment.get().getTransportType());

            totalDist += segment.get().getDistanceKm();
            totalDur += segment.get().getDurationHours();
        }

        if (transportTypes.size() < 2) {
            return RouteOption.unavailable("Смешанный",
                    "На данном маршруте выгоднее использовать один вид транспорта без пересадок на другие типы");
        }

        return new RouteOption("Смешанный", MathUtils.round(totalDist), MathUtils.round(totalDur), 0.0, segments);
    }
}