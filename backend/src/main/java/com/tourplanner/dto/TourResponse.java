package com.tourplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tourplanner.model.TransportType;

// Ausgehende Tour-Daten als API-Response (nur Output-Felder, keine Validation).
public record TourResponse(
        Long id,
        String name,
        String description,
        @JsonProperty("from") String fromPoint,
        @JsonProperty("to") String toPoint,
        TransportType transportType,
        double distance,
        long estimatedTime,
        String image,
        String routeGeometry,
        int popularity,
        String childFriendliness
) {
}
