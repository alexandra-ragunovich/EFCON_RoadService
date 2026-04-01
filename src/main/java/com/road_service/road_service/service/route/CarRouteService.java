package com.road_service.road_service.service.route;

import com.road_service.road_service.dto.dto.RouteOptionDTO;
import com.road_service.road_service.dto.dto.RouteSegmentDTO;
import com.road_service.road_service.entity.CityEntity;
import com.road_service.road_service.integretion.GeoRoutingClient;
import com.road_service.road_service.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarRouteService {

    private final GeoRoutingClient geoRoutingClient;

    public RouteOptionDTO build(List<CityEntity> chain) {

        List<RouteSegmentDTO> segments = new ArrayList<>();

        double totalDist = 0;
        double totalDur = 0;

        for (int i = 0; i < chain.size() - 1; i++) {

            CityEntity from = chain.get(i);
            CityEntity to = chain.get(i + 1);

            double[] routeInfo = geoRoutingClient.getGroundRouteInfo(
                    from.getLatitude(), from.getLongitude(),
                    to.getLatitude(), to.getLongitude()
            );

            double distKm = routeInfo[0];
            double durHrs = routeInfo[1];

            segments.add(new RouteSegmentDTO(
                    from.getName(), from.getCountry(),
                    to.getName(), to.getCountry(),
                    "АВТО", MathUtils.round(distKm), MathUtils.round(durHrs), 0.0
            ));

            totalDist += distKm;
            totalDur += durHrs;
        }

        return  RouteOptionDTO.available("Автомобиль", MathUtils.round(totalDist), MathUtils.round(totalDur), 0.0, segments);
    }
}