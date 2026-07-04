package com.tourplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tourplanner.model.TransportType;

import java.util.List;

public record TourExportDto(
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
        String childFriendliness,
        List<TourLogDto> logs
) {
    public static TourExportDto from(TourResponse tour, List<TourLogDto> logs) {
        return new TourExportDto(
                tour.id(),
                tour.name(),
                tour.description(),
                tour.fromPoint(),
                tour.toPoint(),
                tour.transportType(),
                tour.distance(),
                tour.estimatedTime(),
                tour.image(),
                tour.routeGeometry(),
                tour.popularity(),
                tour.childFriendliness(),
                logs
        );
    }
}
