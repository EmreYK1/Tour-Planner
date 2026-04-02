// backend/src/main/java/com/tourplanner/dto/TourDto.java
// Datenübertragungsobjekt für Tour-JSON (API); Felder passen zum Angular-Modell.
package com.tourplanner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tourplanner.model.TransportType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TourDto(
        Long id,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 10_000) String description,
        @NotBlank @Size(max = 500) @JsonProperty("from") String fromPoint,
        @NotBlank @Size(max = 500) @JsonProperty("to") String toPoint,
        @NotNull TransportType transportType,
        @DecimalMin("0.0") double distance,
        @Min(0) long estimatedTime,
        @Size(max = 2000) String image
) {
}
