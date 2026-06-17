package com.road_service.road_service.integretion;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class GeoRoutingClient {

    private static final String OSRM_BASE_URL = "http://router.project-osrm.org/route/v1/driving/";
    private static final String OSRM_QUERY_PARAMS = "?overview=false";
    private static final String JSON_KEY_ROUTES = "routes";
    private static final String JSON_KEY_DISTANCE = "distance";
    private static final String JSON_KEY_DURATION = "duration";
    private static final double METERS_IN_KM = 1000.0;
    private static final double SECONDS_IN_HOUR = 3600.0;
    private static final double FALLBACK_DISTANCE_KM = 500.0;
    private static final double FALLBACK_DURATION_HOURS = 5.0;

    private final RestClient restClient = RestClient.create();
    @Value("${apininjas.api.key}")
    private String ninjasApiKey;



    public double[] getGroundRouteInfo(double latFrom, double lonFrom, double latTo, double lonTo) {
        try {
            String coords = lonFrom + "," + latFrom + ";" + lonTo + "," + latTo;
            JsonNode response = restClient.get()
                    .uri(OSRM_BASE_URL + coords + OSRM_QUERY_PARAMS)
                    .retrieve().body(JsonNode.class);

            JsonNode route = response.get(JSON_KEY_ROUTES).get(0);
            return new double[]{ route.get(JSON_KEY_DISTANCE).asDouble() / METERS_IN_KM, route.get(JSON_KEY_DURATION).asDouble() / SECONDS_IN_HOUR };
        } catch (Exception e) {
            return new double[]{FALLBACK_DISTANCE_KM, FALLBACK_DURATION_HOURS};
        }
    }

}
