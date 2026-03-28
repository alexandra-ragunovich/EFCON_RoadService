package com.road_service.road_service.service.segment;

import com.road_service.road_service.dto.request.RouteSegmentRequest;
import com.road_service.road_service.entity.CityEntity;
import com.road_service.road_service.entity.TransportRouteEntity;
import com.road_service.road_service.repository.TransportRouteRepository;
import com.road_service.road_service.service.GeoCalculator;
import com.road_service.road_service.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RouteSegmentService {

    private final TransportRouteRepository transportRouteRepository;
    private final GeoCalculator geoCalculator;


    public Optional<RouteSegmentRequest> findSegment(
            CityEntity from,
            CityEntity to,
            String transportType,
            SegmentProvider provider) {

        Optional<RouteSegmentRequest> dbResult = findInDatabase(from, to, transportType);
        if (dbResult.isPresent()) {
            return dbResult;
        }

        return provider.findSegment(from, to, transportType);
    }

    private Optional<RouteSegmentRequest> findInDatabase(CityEntity from, CityEntity to, String transportType) {
        List<TransportRouteEntity> forwardRoutes = transportRouteRepository.findByFromCityId(from.getId());

        Optional<TransportRouteEntity> directMatch = forwardRoutes.stream()
                .filter(r -> r.getToCity().getId() == to.getId() && r.getTransportType().equals(transportType))
                .findFirst();

        if (directMatch.isPresent()) {
            return Optional.of(mapToRequest(directMatch.get(), from, to));
        }

        List<TransportRouteEntity> backwardRoutes = transportRouteRepository.findByFromCityId(to.getId());
        Optional<TransportRouteEntity> reverseMatch = backwardRoutes.stream()
                .filter(r -> r.getToCity().getId() == from.getId() && r.getTransportType().equals(transportType))
                .findFirst();

        if (reverseMatch.isPresent()) {
            return Optional.of(mapToRequest(reverseMatch.get(), from, to));
        }

        return Optional.empty();
    }

    private RouteSegmentRequest mapToRequest(TransportRouteEntity route, CityEntity from, CityEntity to) {
        double dist = geoCalculator.distance(from, to) * 1.2;
        return new RouteSegmentRequest(
                from.getName(),
                from.getCountry(),
                to.getName(),
                to.getCountry(),
                route.getTransportType(),
                MathUtils.round(dist),
                MathUtils.round(route.getDurationHours()),
                0.0
        );
    }


    public interface SegmentProvider {
        Optional<RouteSegmentRequest> findSegment(CityEntity from, CityEntity to, String transportType);
    }
}