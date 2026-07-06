package com.tourplanner.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tourplanner.dto.CreateTourRequest;
import com.tourplanner.dto.TourResponse;
import com.tourplanner.model.Tour;
import com.tourplanner.model.TransportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TourMapperTest {

    private TourMapper sut;

    @BeforeEach
    void setUp() {
        sut = new TourMapper();
    }

    // ── Fixture helpers ────────────────────────────────────────────────────────

    private static Tour fullTourEntity() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setName("Vienna → Salzburg");
        tour.setDescription("Scenic route through the Alps");
        tour.setFromLocation("Vienna");
        tour.setToLocation("Salzburg");
        tour.setTransportType(TransportType.CAR);
        tour.setDistance(295.0);
        tour.setEstimatedTime(9000L);
        tour.setImage("route.png");
        tour.setRouteGeometry("{\"type\":\"LineString\"}");
        return tour;
    }

    private static CreateTourRequest fullCreateRequest() {
        return new CreateTourRequest(
                "Vienna → Salzburg",
                "Scenic route through the Alps",
                "Vienna",
                "Salzburg",
                TransportType.CAR,
                295.0,
                9000L,
                "route.png",
                null, null, null, null
        );
    }

    // ── toResponse ─────────────────────────────────────────────────────────────

    @Test
    void tourToResponse_mapsAllFields() {
        Tour entity = fullTourEntity();

        TourResponse result = sut.toResponse(entity, 5, "Niedrig");

        assertThat(result.id()).isEqualTo(entity.getId());
        assertThat(result.name()).isEqualTo(entity.getName());
        assertThat(result.description()).isEqualTo(entity.getDescription());
        assertThat(result.fromPoint()).isEqualTo(entity.getFromLocation());
        assertThat(result.toPoint()).isEqualTo(entity.getToLocation());
        assertThat(result.transportType()).isEqualTo(entity.getTransportType());
        assertThat(result.distance()).isEqualTo(entity.getDistance());
        assertThat(result.estimatedTime()).isEqualTo(entity.getEstimatedTime());
        assertThat(result.image()).isEqualTo(entity.getImage());
        assertThat(result.routeGeometry()).isEqualTo(entity.getRouteGeometry());
        assertThat(result.popularity()).isEqualTo(5);
        assertThat(result.childFriendliness()).isEqualTo("Niedrig");
    }

    // ── toNewEntity ────────────────────────────────────────────────────────────

    @Test
    void requestToTour_mapsAllFields() {
        CreateTourRequest request = fullCreateRequest();

        Tour result = sut.toNewEntity(request);

        assertThat(result.getName()).isEqualTo(request.name());
        assertThat(result.getDescription()).isEqualTo(request.description());
        assertThat(result.getFromLocation()).isEqualTo(request.from());
        assertThat(result.getToLocation()).isEqualTo(request.to());
        assertThat(result.getTransportType()).isEqualTo(request.transportType());
        assertThat(result.getDistance()).isEqualTo(request.distance());
        assertThat(result.getEstimatedTime()).isEqualTo(request.estimatedTime());
        assertThat(result.getImage()).isEqualTo(request.image());
    }

    // ── apply (update) ─────────────────────────────────────────────────────────

    @Test
    void apply_updatesExistingEntity() {
        Tour existing = fullTourEntity();
        CreateTourRequest update = new CreateTourRequest(
                "Updated Name", "New description", "Linz", "Graz",
                TransportType.BICYCLE, 150.0, 5400L, "new.png",
                null, null, null, null);

        sut.apply(update, existing);

        assertThat(existing.getName()).isEqualTo(update.name());
        assertThat(existing.getDescription()).isEqualTo(update.description());
        assertThat(existing.getFromLocation()).isEqualTo(update.from());
        assertThat(existing.getToLocation()).isEqualTo(update.to());
        assertThat(existing.getTransportType()).isEqualTo(update.transportType());
        assertThat(existing.getDistance()).isEqualTo(update.distance());
        assertThat(existing.getEstimatedTime()).isEqualTo(update.estimatedTime());
        assertThat(existing.getImage()).isEqualTo(update.image());
    }

    // Regressionstest: Ein Update ohne mitgeschickte Koordinaten darf zuvor
    // gespeicherte Koordinaten nicht löschen (sonst bricht das Wetter-Feature
    // für Touren, die z. B. per Postman ohne fromLon/fromLat aktualisiert werden).
    @Test
    void apply_updateWithoutCoordinates_keepsExistingCoordinates() {
        Tour entity = new Tour();
        entity.setFromLon(16.3738);
        entity.setFromLat(48.2082);
        entity.setToLon(16.2311);
        entity.setToLat(47.9967);

        CreateTourRequest requestWithoutCoordinates = new CreateTourRequest(
                "Wienerwald Tour", "Beschreibung", "Wien", "Baden",
                TransportType.BICYCLE, 35.2, 7200L, null,
                null, null, null, null);

        sut.apply(requestWithoutCoordinates, entity);

        assertThat(entity.getFromLon()).isEqualTo(16.3738);
        assertThat(entity.getFromLat()).isEqualTo(48.2082);
        assertThat(entity.getToLon()).isEqualTo(16.2311);
        assertThat(entity.getToLat()).isEqualTo(47.9967);
    }
}
