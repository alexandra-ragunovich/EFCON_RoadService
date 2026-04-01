package com.road_service.road_service.mapper;

import com.road_service.road_service.dto.dto.CityDTO;
import com.road_service.road_service.entity.CityEntity;
import lombok.Data;

@Data
public class CityMapper {
    public static CityDTO toDto(CityEntity entity) {
        if (entity == null) return null;

        CityDTO dto = new CityDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCountry(entity.getCountry());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setIataCode(entity.getIataCode());

        return dto;
    }

    public static CityEntity toEntity(CityDTO dto) {
        if (dto == null) return null;

        CityEntity entity = new CityEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCountry(dto.getCountry());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setIataCode(dto.getIataCode());

        return entity;
    }
}
