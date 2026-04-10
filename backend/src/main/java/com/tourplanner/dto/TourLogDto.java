package com.tourplanner.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record TourLogDto (
    Long id,
    Long tourId,
    @NotNull LocalDateTime dateTime,
    String comment,
    @Min(1) @Max(5) int difficulty,
    @DecimalMin("0.0") double totalDistance,
    @Min(0) long totalTime,
    @Min(1) @Max(5) int rating
) {
}
