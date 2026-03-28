package com.road_service.road_service.mapper;

import com.road_service.road_service.dto.request.CityDto;
import com.road_service.road_service.entity.CityEntity;
import lombok.Data;

@Data
public class CityMapper {
    public static CityDto toDto(CityEntity entity) {
        if (entity == null) return null;

        CityDto dto = new CityDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCountry(entity.getCountry());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setIataCode(entity.getIataCode());

        return dto;
    }

    public static CityEntity toEntity(CityDto dto) {
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
