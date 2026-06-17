package com.road_service.road_service.repository;

import com.road_service.road_service.entity.TransportRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransportRouteRepository extends JpaRepository<TransportRouteEntity, Long> {

    List<TransportRouteEntity> findByFromCityId(Long fromCityId);
}
