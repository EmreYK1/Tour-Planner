// backend/src/main/java/com/tourplanner/mapper/TourMapper.java
// Wandelt JPA-Entity Tour in TourResponse um und mapped CreateTourRequest auf die Entity.
package com.tourplanner.mapper;

import com.tourplanner.dto.CreateTourRequest;
import com.tourplanner.dto.TourResponse;
import com.tourplanner.model.Tour;
import org.springframework.stereotype.Component;

@Component
public class TourMapper {

    public TourResponse toResponse(Tour entity, int popularity, String childFriendliness) {
        return new TourResponse(
                entity.getId(),
                entity.getName(),
                nullToEmpty(entity.getDescription()),
                entity.getFromLocation(),
                entity.getToLocation(),
                entity.getTransportType(),
                entity.getDistance(),
                entity.getEstimatedTime(),
                nullToEmpty(entity.getImage()),
                entity.getRouteGeometry(),
                popularity,
                childFriendliness
        );
    }

    public Tour toNewEntity(CreateTourRequest request) {
        Tour tour = new Tour();
        apply(request, tour);
        return tour;
    }

    public void apply(CreateTourRequest request, Tour entity) {
        entity.setName(request.name());
        entity.setDescription(emptyToNull(request.description()));
        entity.setFromLocation(request.from());
        entity.setToLocation(request.to());
        entity.setTransportType(request.transportType());
        entity.setDistance(request.distance());
        entity.setEstimatedTime(request.estimatedTime());
        entity.setImage(emptyToNull(request.image()));
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
