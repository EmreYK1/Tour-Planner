package com.tourplanner.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tourplanner.dto.GeocodingResultDto;
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

    // Ruft die ORS Directions API auf; gibt Distanz (m), Dauer (s) und GeoJSON-Geometrie zurück.
    public OrsRouteResult fetchRoute(TransportType transportType, List<List<Double>> coordinates) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new OrsApiException("ORS API key is not configured (ors.api.key)");
        }
        if (coordinates == null || coordinates.size() < 2) {
            throw new OrsApiException("At least two coordinates are required");
        }

        String profile = toOrsProfile(transportType);
        OrsDirectionsRequest requestBody = OrsDirectionsRequest.forRoute(coordinates);

        // /geojson Endpoint liefert in ORS v9 direkte GeoJSON-Koordinaten ohne Encoding
        OrsGeoJsonResponse response = restClient.post()
                .uri("/v2/directions/{profile}/geojson", profile)
                .header("Authorization", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
                    String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    throw new OrsApiException(
                            "OpenRouteService error " + res.getStatusCode().value() + ": " + body);
                })
                .body(OrsGeoJsonResponse.class);

        return toResult(response);
    }

    private static OrsRouteResult toResult(OrsGeoJsonResponse response) {
        if (response == null || response.features() == null || response.features().isEmpty()) {
            throw new OrsApiException("ORS response contains no features");
        }

        OrsGeoJsonResponse.Feature feature = response.features().get(0);
        if (feature.properties() == null || feature.properties().summary() == null) {
            throw new OrsApiException("ORS response missing summary");
        }
        if (feature.geometry() == null) {
            throw new OrsApiException("ORS response missing geometry");
        }

        return new OrsRouteResult(
                feature.properties().summary().distance(),
                feature.properties().summary().duration(),
                feature.geometry());
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

    // Löst eine Freitextadresse in lon/lat-Koordinaten auf; gibt den ersten ORS-Treffer zurück.
    public GeocodingResultDto fetchGeocode(String address) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new OrsApiException("ORS API key is not configured (ors.api.key)");
        }

        OrsGeocodeResponse response = restClient.get()
                .uri("/geocode/search?api_key={key}&text={text}&size=1", apiKey, address)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
                    String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    throw new OrsApiException(
                            "ORS Geocoding error " + res.getStatusCode().value() + ": " + body);
                })
                .body(OrsGeocodeResponse.class);

        if (response == null || response.features() == null || response.features().isEmpty()) {
            throw new OrsApiException("No geocoding result found for: " + address);
        }

        List<Double> coords = response.features().get(0).geometry().coordinates();
        // ORS liefert Koordinaten als [lon, lat]
        return new GeocodingResultDto(coords.get(0), coords.get(1));
    }

    private record OrsDirectionsRequest(
            List<List<Double>> coordinates,
            boolean geometry,
            boolean instructions) {

        static OrsDirectionsRequest forRoute(List<List<Double>> coordinates) {
            return new OrsDirectionsRequest(coordinates, true, false);
        }
    }

    private record OrsGeoJsonResponse(List<Feature> features) {
        private record Feature(Properties properties, OrsRouteResponse.Route.Geometry geometry) {}
        private record Properties(Summary summary) {}
        private record Summary(double distance, double duration) {}
    }

    private record OrsGeocodeResponse(List<OrsGeocodeFeature> features) {}
    private record OrsGeocodeFeature(OrsGeocodeGeometry geometry) {}
    private record OrsGeocodeGeometry(List<Double> coordinates) {}
}
