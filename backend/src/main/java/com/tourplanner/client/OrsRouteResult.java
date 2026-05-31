package com.tourplanner.client;

/**
 * Aus ORS extrahierte Routendaten: Distanz (m), Dauer (s), GeoJSON LineString.
 */
public record OrsRouteResult(
        double distanceMeters,
        double durationSeconds,
        OrsRouteResponse.Route.Geometry geometry
) {
}
