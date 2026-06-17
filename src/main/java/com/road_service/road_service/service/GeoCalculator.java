package com.road_service.road_service.service;

import com.road_service.road_service.entity.CityEntity;
import org.springframework.stereotype.Component;

@Component
public class GeoCalculator {

    public double distance(CityEntity start, CityEntity dest) {
        double dLat = Math.toRadians(dest.getLatitude() - start.getLatitude());
        double dLon = Math.toRadians(dest.getLongitude() - start.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(start.getLatitude())) * Math.cos(Math.toRadians(dest.getLatitude())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 6371 * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    public double calculateAzimuth(CityEntity start, CityEntity dest) {
        double lat1 = Math.toRadians(start.getLatitude());
        double lon1 = Math.toRadians(start.getLongitude());
        double lat2 = Math.toRadians(dest.getLatitude());
        double lon2 = Math.toRadians(dest.getLongitude());
        double y = Math.sin(lon2 - lon1) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1);
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }
}
 