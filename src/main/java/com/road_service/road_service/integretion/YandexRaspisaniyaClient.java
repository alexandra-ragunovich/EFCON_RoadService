package com.road_service.road_service.integretion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class YandexRaspisaniyaClient {

    private static final String TRANSPORT_TRAIN = "ПОЕЗД";
    private static final String TRANSPORT_FLIGHT = "САМОЛЕТ";
    private static final String TRANSPORT_SUBURBAN = "ЭЛЕКТРИЧКА";
    private static final String TRANSPORT_BUS = "АВТОБУС";
    private static final String TRANSPORT_MINIBUS = "МАРШРУТКА";
    private static final String YANDEX_TYPE_TRAIN = "train";
    private static final String YANDEX_TYPE_SUBURBAN = "suburban";
    private static final String YANDEX_TYPE_PLANE = "plane";
    private static final String YANDEX_TYPE_BUS = "bus";
    private static final String YANDEX_TYPE_UNKNOWN = "unknown";
    private static final double METERS_IN_KM = 1000.0;
    private static final double SECONDS_IN_HOUR = 3600.0;
    private static final String SEARCH_ENDPOINT = "/search/?apikey=";
    private static final String STATIONS_LIST_ENDPOINT = "/stations_list/?apikey=";
    private static final String FLIGHT_SEARCH_PARAMS = "&transport_types=plane&system=iata&format=json&limit=1";
    private static final String ROUTE_SEARCH_PARAMS = "&format=json&limit=1";
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, List<StationInfo>> cityStationsCache = new ConcurrentHashMap<>();

    @Value("${yandex.rasp.api.key}")
    private String apiKey;

    @Value("${yandex.rasp.api.url:https://api.rasp.yandex.net/v3.0}")
    private String apiUrl;

    public YandexRaspisaniyaClient() {
        this.restClient = RestClient.builder().build();
    }

    private record StationInfo(String city, String name, String code, String type) {}


    public double[] findRoute(String fromCity, String toCity, String transportType) {

        System.out.println("Ищу маршрут: " + fromCity + " -> " + toCity + " (" + transportType + ")");
        loadStationsForCity(fromCity);
        loadStationsForCity(toCity);

        String fromCode = findBestStationCode(fromCity, transportType);
        String toCode = findBestStationCode(toCity, transportType);
        System.out.println("Коды станций: " + fromCode + " -> " + toCode);
        if (fromCode == null || toCode == null) return null;

        return findRouteByCodes(fromCode, toCode, transportType);
    }

    public double[] findFlightByIata(String fromIata, String toIata) {
        try {
            String url = apiUrl + SEARCH_ENDPOINT + apiKey
                    + "&from=" + fromIata
                    + "&to=" + toIata
                    + FLIGHT_SEARCH_PARAMS;

            String body = restClient.get().uri(url).accept(MediaType.APPLICATION_JSON)
                    .retrieve().body(String.class);

            return parseRoute(body);
        } catch (Exception e) {
            return null;
        }
    }


    private void loadStationsForCity(String cityName) {
        if (cityStationsCache.containsKey(cityName)) return;

        String url = apiUrl + STATIONS_LIST_ENDPOINT + apiKey + "&format=json&lang=ru_RU";

        try {
            String body = restClient.get().uri(url).accept(MediaType.APPLICATION_JSON)
                    .retrieve().body(String.class);

            if (body == null || body.startsWith("<")) return;

            List<StationInfo> stations = parseStationsStreaming(body, cityName);
            cityStationsCache.put(cityName, stations);

        } catch (Exception ignored) {}
    }

    private List<StationInfo> parseStationsStreaming(String json, String targetCity) throws Exception {
        List<StationInfo> result = new ArrayList<>();

        try (JsonParser parser = objectMapper.getFactory().createParser(json)) {

            String currentCity = null;
            String stationName = null;
            String transportType = null;
            String yandexCode = null;

            while (!parser.isClosed()) {
                var token = parser.nextToken();
                if (token == null) break;

                String field = parser.getCurrentName();

                if ("title".equals(field)) {
                    parser.nextToken();
                    String title = parser.getValueAsString();

                    if (matchesCity(title, targetCity)) {
                        currentCity = title;
                    } else {
                        currentCity = null;
                    }
                }

                if (currentCity != null) {
                    if ("station_name".equals(field)) {
                        parser.nextToken();
                        stationName = parser.getValueAsString();
                    }

                    if ("transport_type".equals(field)) {
                        parser.nextToken();
                        transportType = parser.getValueAsString();
                    }

                    if ("yandex_code".equals(field)) {
                        parser.nextToken();
                        yandexCode = parser.getValueAsString();

                        result.add(new StationInfo(
                                currentCity,
                                stationName != null ? stationName : currentCity,
                                yandexCode,
                                transportType != null ? transportType : YANDEX_TYPE_UNKNOWN
                        ));

                        stationName = null;
                        transportType = null;
                        yandexCode = null;
                    }
                }
            }
        }

        return result;
    }

    private boolean matchesCity(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        return a.contains(b) || b.contains(a);
    }


    private String findBestStationCode(String city, String transportType) {
        List<StationInfo> stations = cityStationsCache.getOrDefault(city, List.of());
        if (stations.isEmpty()) return null;

        String type = translateTransport(transportType);

        if (type.equals(YANDEX_TYPE_TRAIN)) {
            String code = stations.stream()
                    .filter(s -> (s.type().equals(YANDEX_TYPE_TRAIN) || s.type().equals(YANDEX_TYPE_SUBURBAN)))
                    .filter(s -> matchesCity(s.city(), city))
                    .map(StationInfo::code)
                    .findFirst()
                    .orElse(null);

            if (code != null) return code;

            return stations.stream()
                    .filter(s -> s.type().equals(YANDEX_TYPE_TRAIN) || s.type().equals(YANDEX_TYPE_SUBURBAN))
                    .map(StationInfo::code)
                    .findFirst()
                    .orElse(null);
        }

        if (type.equals(YANDEX_TYPE_SUBURBAN)) {
            return stations.stream()
                    .filter(s -> s.type().equals(YANDEX_TYPE_SUBURBAN))
                    .map(StationInfo::code)
                    .findFirst()
                    .orElse(null);
        }

        return stations.stream()
                .filter(s -> s.type().equals(type))
                .map(StationInfo::code)
                .findFirst()
                .orElse(null);
    }


    private double[] findRouteByCodes(String from, String to, String transportType) {
        try {
            String url = apiUrl +SEARCH_ENDPOINT + apiKey
                    + "&from=" + from
                    + "&to=" + to
                    + "&transport_types=" + translateTransport(transportType)
                    + ROUTE_SEARCH_PARAMS;

            String body = restClient.get().uri(url).accept(MediaType.APPLICATION_JSON)
                    .retrieve().body(String.class);

            return parseRoute(body);

        } catch (Exception e) {
            return null;
        }
    }

    private double[] parseRoute(String body) throws Exception {
        if (body == null || body.startsWith("<")) return null;

        var root = objectMapper.readTree(body);
        var segments = root.get("segments");
        if (segments == null || segments.isEmpty()) return null;

        var seg = segments.get(0);

        double durationHours = seg.get("duration").asDouble() / SECONDS_IN_HOUR;
        double distanceKm = seg.has("distance") && !seg.get("distance").isNull()
                ? seg.get("distance").asDouble() / METERS_IN_KM
                : 0.0;

        return new double[]{durationHours, distanceKm};
    }

    private String translateTransport(String t) {
        return switch (t.toUpperCase()) {
            case TRANSPORT_TRAIN -> YANDEX_TYPE_TRAIN;
            case TRANSPORT_FLIGHT -> YANDEX_TYPE_PLANE;
            case TRANSPORT_SUBURBAN -> YANDEX_TYPE_SUBURBAN;
            case TRANSPORT_BUS , TRANSPORT_MINIBUS -> YANDEX_TYPE_BUS;
            default -> YANDEX_TYPE_BUS;
        };
    }
}
