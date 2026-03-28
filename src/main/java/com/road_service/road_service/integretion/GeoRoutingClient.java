package com.road_service.road_service.integretion;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class GeoRoutingClient {

    private final RestClient restClient = RestClient.create();
    @Value("${apininjas.api.key}")
    private String ninjasApiKey;



    public double[] getGroundRouteInfo(double latFrom, double lonFrom, double latTo, double lonTo) {
        try {
            String coords = lonFrom + "," + latFrom + ";" + lonTo + "," + latTo;
            JsonNode response = restClient.get()
                    .uri("http://router.project-osrm.org/route/v1/driving/" + coords + "?overview=false")
                    .retrieve().body(JsonNode.class);

            JsonNode route = response.get("routes").get(0);
            return new double[]{ route.get("distance").asDouble() / 1000.0, route.get("duration").asDouble() / 3600.0 };
        } catch (Exception e) {
            return new double[]{500.0, 5.0};
        }
    }

}
