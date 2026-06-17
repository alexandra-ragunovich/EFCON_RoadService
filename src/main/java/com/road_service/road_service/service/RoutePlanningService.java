package com.road_service.road_service.service;

import com.road_service.road_service.dto.request.RouteBuildRequest;
import com.road_service.road_service.dto.dto.RouteOptionDTO;
import com.road_service.road_service.dto.dto.RouteSegmentDTO;
import com.road_service.road_service.dto.response.FullRouteResponse;
import com.road_service.road_service.dto.dto.TravelAdviceDTO;
import com.road_service.road_service.entity.CityEntity;
import com.road_service.road_service.dto.dto.CityDTO;
import com.road_service.road_service.grpc.IntegrationGrpcClient;
import com.road_service.road_service.repository.CityRepository;
import com.road_service.road_service.service.route.CarRouteService;
import com.road_service.road_service.service.route.CombinedRouteService;
import com.road_service.road_service.strategy.*;
import com.road_service.road_service.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoutePlanningService {

    private final CityRepository cityRepository;
    private final CityChainBuilder cityChainBuilder;
    private final CarRouteService carRouteService;
    private final CombinedRouteService combinedRouteService;

    private final TrainSegmentBuilder trainBuilder;
    private final SuburbanSegmentBuilder suburbanBuilder;
    private final BusSegmentBuilder busBuilder;
    private final MarshrutkaSegmentBuilder marshrutkaBuilder;
    private final FlightSegmentBuilder flightBuilder;
    private final IntegrationGrpcClient integrationGrpcClient;
    private final CarbonFootprintSorter ecoProcessor;


    public FullRouteResponse buildRoute(RouteBuildRequest request) {

        CityEntity startCity = findCity(request.getFromCity(), request.getFromCountry());
        CityEntity endCity = findCity(request.getToCity(), request.getToCountry());

        List<CityEntity> carChain = cityChainBuilder.buildCarChain(startCity, endCity, request.getWaypointsCount());
        List<CityEntity> transportChain = cityChainBuilder.buildTransportChain(startCity, endCity, request.getWaypointsCount());
        List<RouteOptionDTO> options = new ArrayList<>();

        RouteOptionDTO carOption = carRouteService.build(carChain);
        carOption.setRouteName("Автомобиль");
        options.add(carOption);
        options.add(buildSingleTransportRoute("Поезд", transportChain, trainBuilder));
        options.add(buildSingleTransportRoute("Электричка", transportChain, suburbanBuilder));
        options.add(buildSingleTransportRoute("Автобус", transportChain, busBuilder));
        options.add(buildSingleTransportRoute("Маршрутка", transportChain, marshrutkaBuilder));
        options.add(buildSingleTransportRoute("Авиаперелет", transportChain, flightBuilder));
        options.add(combinedRouteService.buildMixedRoute(transportChain));

        ecoProcessor.processAndSort(options);

        TravelAdviceDTO advice = null;

        try {
            advice = integrationGrpcClient.fetchTravelAdvice(
                    endCity.getName(),
                    endCity.getCountry()
            );
        } catch (Exception e) {
            System.err.println("Не удалось получить советы: " + e.getMessage());
        }

        return new FullRouteResponse(
                formatCity(startCity),
                formatCity(endCity),
                options,
                advice
        );
    }

    private RouteOptionDTO buildSingleTransportRoute(String routeName, List<CityEntity> chain, SegmentBuilder builder) {

        List<RouteSegmentDTO> segments = new ArrayList<>();
        double totalDist = 0;
        double totalDur = 0;

        for (int i = 0; i < chain.size() - 1; i++) {

            CityEntity from = chain.get(i);
            CityEntity to = chain.get(i + 1);

            Optional<RouteSegmentDTO> segment = builder.buildSegment(from, to);
            if (segment.isEmpty()) {
                return RouteOptionDTO.unavailable(routeName,
                        String.format("Нет рейса (%s) между г. %s и г. %s", routeName.toLowerCase(), from.getName(), to.getName()));
            }

            segments.add(segment.get());
            totalDist += segment.get().getDistanceKm();
            totalDur += segment.get().getDurationHours();
        }

        return  RouteOptionDTO.available(routeName, MathUtils.round(totalDist), MathUtils.round(totalDur), 0.0, segments);
    }
    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream().map(city -> {
            CityDTO dto = new CityDTO();
            dto.setId(city.getId());
            dto.setName(city.getName());
            dto.setCountry(city.getCountry());
            dto.setLatitude(city.getLatitude());
            dto.setLongitude(city.getLongitude());
            dto.setIataCode(city.getIataCode());
            return dto;
        }).toList();
    }
    private CityEntity findCity(String name, String country) {

        return cityRepository.findByNameAndCountry(name, country)
                .orElseThrow(() -> new RuntimeException("Город не найден: " + name + " (" + country + ")"));
    }

    private String formatCity(CityEntity city) {
        return city.getName() + " (" + city.getCountry() + ")";
    }


}