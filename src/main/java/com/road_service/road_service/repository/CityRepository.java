package com.road_service.road_service.repository;

import com.road_service.road_service.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity,Long> {

    Optional<CityEntity> findByNameAndCountry(String name, String country);
    List<CityEntity> findByLatitudeBetweenAndLongitudeBetween(
            double minLat, double maxLat,
            double minLon, double maxLon
    );
}
