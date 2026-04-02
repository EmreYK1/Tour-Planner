// backend/src/main/java/com/tourplanner/mapper/TourMapper.java
// Wandelt JPA-Entity Tour in TourDto um und umgekehrt.
package com.tourplanner.mapper;

import com.tourplanner.dto.TourDto;
import com.tourplanner.model.Tour;
import org.springframework.stereotype.Component;

@Component
public class TourMapper {

    public TourDto toDto(Tour entity) {
        return new TourDto(
                entity.getId(),
                entity.getName(),
                nullToEmpty(entity.getDescription()),
                entity.getFromLocation(),
                entity.getToLocation(),
                entity.getTransportType(),
                entity.getDistance(),
                entity.getEstimatedTime(),
                nullToEmpty(entity.getImage())
        );
    }

    public Tour toNewEntity(TourDto dto) {
        Tour tour = new Tour();
        apply(dto, tour);
        return tour;
    }

    public void apply(TourDto dto, Tour entity) {
        entity.setName(dto.name());
        entity.setDescription(emptyToNull(dto.description()));
        entity.setFromLocation(dto.fromPoint());
        entity.setToLocation(dto.toPoint());
        entity.setTransportType(dto.transportType());
        entity.setDistance(dto.distance());
        entity.setEstimatedTime(dto.estimatedTime());
        entity.setImage(emptyToNull(dto.image()));
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
