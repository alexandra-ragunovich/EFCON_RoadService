package com.road_service.road_service.dto.request;

import lombok.Data;

@Data
public class CityDto {

    private Long id;
    private String name;
    private String country;
    private double latitude;
    private double longitude;
    private String iataCode;
}
