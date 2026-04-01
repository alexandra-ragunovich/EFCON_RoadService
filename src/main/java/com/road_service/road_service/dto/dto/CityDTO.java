package com.road_service.road_service.dto.dto;

import lombok.Data;

@Data
public class CityDTO {

    private Long id;
    private String name;
    private String country;
    private double latitude;
    private double longitude;
    private String iataCode;
}
