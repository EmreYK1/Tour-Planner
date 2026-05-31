package com.tourplanner.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tourplanner.exception.OrsApiException;
import com.tourplanner.model.TransportType;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OrsClient {

    private static final String ORS_BASE_URL = "https://api.openrouteservice.org";

    private final RestClient restClient;
    private final String apiKey;

    public OrsClient(@Value("${ors.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(ORS_BASE_URL)
                .build();
    }

    /**
     * Ruft die ORS Directions API auf.
     *
     * @param coordinates ORS-Format: [[lon, lat], [lon, lat], ...], mindestens 2 Punkte
     * @return Distanz (m), Dauer (s), GeoJSON LineString-Geometrie
     */
    public OrsRouteResult fetchRoute(TransportType transportType, List<List<Double>> coordinates) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new OrsApiException("ORS API key is not configured (ors.api.key)");
        }
        if (coordinates == null || coordinates.size() < 2) {
            throw new OrsApiException("At least two coordinates are required");
        }

        String profile = toOrsProfile(transportType);
        OrsDirectionsRequest requestBody = OrsDirectionsRequest.forRoute(coordinates);

        OrsRouteResponse response = restClient.post()
                .uri("/v2/directions/{profile}", profile)
                .header("Authorization", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
                    String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    throw new OrsApiException(
                            "OpenRouteService error " + res.getStatusCode().value() + ": " + body);
                })
                .body(OrsRouteResponse.class);

        return toResult(response);
    }

    private static OrsRouteResult toResult(OrsRouteResponse response) {
        if (response == null || response.routes() == null || response.routes().isEmpty()) {
            throw new OrsApiException("ORS response contains no routes");
        }

        OrsRouteResponse.Route route = response.routes().get(0);
        if (route.summary() == null) {
            throw new OrsApiException("ORS response missing summary");
        }
        if (route.geometry() == null) {
            throw new OrsApiException("ORS response missing geometry");
        }

        return new OrsRouteResult(
                route.summary().distance(),
                route.summary().duration(),
                route.geometry());
    }

    private static String toOrsProfile(TransportType transportType) {
        return switch (transportType) {
            case WALK -> "foot-walking";
            case BICYCLE -> "cycling-regular";
            case CAR -> "driving-car";
            case PUBLIC_TRANSPORT ->
                    throw new OrsApiException("Transport type not supported for ORS routing: PUBLIC_TRANSPORT");
        };
    }

    private record OrsDirectionsRequest(
            List<List<Double>> coordinates,
            boolean geometry,
            @JsonProperty("geometry_format") String geometryFormat,
            boolean instructions) {

        static OrsDirectionsRequest forRoute(List<List<Double>> coordinates) {
            return new OrsDirectionsRequest(coordinates, true, "geojson", false);
        }
    }
}
