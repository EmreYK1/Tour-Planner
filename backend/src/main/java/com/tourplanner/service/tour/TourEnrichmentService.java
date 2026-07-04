package com.tourplanner.service.tour;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourplanner.client.OrsClient;
import com.tourplanner.client.OrsRouteResult;
import com.tourplanner.dto.CreateTourRequest;
import com.tourplanner.model.Tour;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TourEnrichmentService {

    private static final Logger log = LoggerFactory.getLogger(TourEnrichmentService.class);

    private final OrsClient orsClient;
    private final ObjectMapper objectMapper;

    public TourEnrichmentService(OrsClient orsClient, ObjectMapper objectMapper) {
        this.orsClient = orsClient;
        this.objectMapper = objectMapper;
    }

    // Reichert die Tour mit ORS-Routendaten an; schlägt die Abfrage fehl, bleiben bisherige Werte erhalten.
    public void enrichWithRouteData(Tour entity, CreateTourRequest request) {
        if (!hasCoordinates(request)) {
            return;
        }
        try {
            List<List<Double>> coords = List.of(
                    List.of(request.fromLon(), request.fromLat()),
                    List.of(request.toLon(), request.toLat()));
            OrsRouteResult result = orsClient.fetchRoute(request.transportType(), coords);
            entity.setDistance(result.distanceMeters() / 1000.0);
            entity.setEstimatedTime((long) result.durationSeconds());
            entity.setRouteGeometry(objectMapper.writeValueAsString(result.geometry()));
        } catch (JsonProcessingException e) {
            log.warn("ORS-Geometrie konnte nicht serialisiert werden: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("ORS-Routenabfrage fehlgeschlagen, behalte User-Werte: {}", e.getMessage());
        }
    }

    private boolean hasCoordinates(CreateTourRequest request) {
        return request.fromLon() != null && request.fromLat() != null
                && request.toLon() != null && request.toLat() != null;
    }
}
