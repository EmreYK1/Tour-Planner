package com.tourplanner.dto;

import com.tourplanner.model.TransportType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Eingehende Daten beim Anlegen oder Aktualisieren einer Tour (nur Input-Felder inkl. Validation).
public record CreateTourRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 10_000) String description,
        @NotBlank @Size(max = 500) String from,
        @NotBlank @Size(max = 500) String to,
        @NotNull TransportType transportType,
        @DecimalMin("0.0") double distance,
        @Min(0) long estimatedTime,
        @Size(max = 2000) String image,
        Double fromLon,
        Double fromLat,
        Double toLon,
        Double toLat
) {
}
