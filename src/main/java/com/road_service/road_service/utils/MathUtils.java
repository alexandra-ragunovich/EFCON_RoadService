package com.road_service.road_service.utils;

public final class MathUtils {
    private MathUtils() {}

    public static double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}