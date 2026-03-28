package com.road_service.road_service.service;

import com.road_service.road_service.entity.CityEntity;
import com.road_service.road_service.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CityChainBuilder {

    private final CityRepository cityRepository;
    private final GeoCalculator geoCalculator;


    public List<CityEntity> buildCarChain(CityEntity start, CityEntity dest, int waypointsCount) {

        List<CityEntity> chain = new ArrayList<>();
        chain.add(start);

        if (waypointsCount <= 0) {
            chain.add(dest);
            return chain;
        }

        CityEntity current = start;

        for (int i = 0; i < waypointsCount; i++) {
            CityEntity next = findNextCityOnRoute(current, dest, chain);
            if (next == null) break;
            chain.add(next);
            current = next;
        }

        chain.add(dest);
        return chain;
    }

    public List<CityEntity> buildTransportChain(CityEntity start, CityEntity dest, int waypointsCount) {

        List<CityEntity> chain = new ArrayList<>();
        chain.add(start);

        if (waypointsCount <= 0) {
            chain.add(dest);
            return chain;
        }

        CityEntity current = start;

        for (int i = 0; i < waypointsCount; i++) {
            CityEntity next = findNextTransportHub(current, dest, chain);
            if (next == null) break;
            chain.add(next);
            current = next;
        }

        chain.add(dest);
        return chain;
    }

    private CityEntity findNextCityOnRoute(CityEntity current, CityEntity dest, List<CityEntity> exclude) {

        double azimuth = geoCalculator.calculateAzimuth(current, dest);
        double distToDest = geoCalculator.distance(current, dest);

        List<CityEntity> candidates = cityRepository.findByLatitudeBetweenAndLongitudeBetween(
                Math.min(current.getLatitude(), dest.getLatitude()) - 3,
                Math.max(current.getLatitude(), dest.getLatitude()) + 3,
                Math.min(current.getLongitude(), dest.getLongitude()) - 3,
                Math.max(current.getLongitude(), dest.getLongitude()) + 3
        );

        return candidates.stream()
                .filter(c -> !exclude.contains(c) && !c.equals(dest))
                .filter(c -> {
                    double angle = Math.abs(geoCalculator.calculateAzimuth(current, c) - azimuth);
                    return Math.min(angle, 360 - angle) <= 30.0;
                })
                .filter(c -> {
                    double dist = geoCalculator.distance(current, c);
                    return dist > 50 && dist < distToDest * 0.6;
                })
                .min(Comparator.comparingDouble(c ->
                        geoCalculator.distance(current, c) + geoCalculator.distance(c, dest)
                ))
                .orElse(null);
    }

    private CityEntity findNextTransportHub(CityEntity current, CityEntity dest, List<CityEntity> exclude) {

        double azimuth = geoCalculator.calculateAzimuth(current, dest);
        double distToDest = geoCalculator.distance(current, dest);

        List<CityEntity> candidates = cityRepository.findByLatitudeBetweenAndLongitudeBetween(
                Math.min(current.getLatitude(), dest.getLatitude()) - 5,
                Math.max(current.getLatitude(), dest.getLatitude()) + 5,
                Math.min(current.getLongitude(), dest.getLongitude()) - 5,
                Math.max(current.getLongitude(), dest.getLongitude()) + 5
        );

        return candidates.stream()
                .filter(c -> !exclude.contains(c) && !c.equals(dest))
                .filter(c -> c.getIataCode() != null || isMajorCity(c))
                .filter(c -> {
                    double angle = Math.abs(geoCalculator.calculateAzimuth(current, c) - azimuth);
                    return Math.min(angle, 360 - angle) <= 45.0;
                })
                .filter(c -> {
                    double dist = geoCalculator.distance(current, c);
                    return dist > 100 && dist < distToDest * 0.7;
                })
                .min(Comparator.comparingDouble(c -> geoCalculator.distance(current, c)))
                .orElse(null);
    }

    private boolean isMajorCity(CityEntity city) {
        return List.of("Минск", "Брест", "Гродно", "Гомель", "Витебск", "Могилёв","Лида").contains(city.getName());
    }
}