package com.tourplanner.dto;

// Aktuelles Wetter am Tour-Startpunkt (aus der OpenWeatherMap Current-Weather-API).
public record WeatherDto(
        String locationName,
        double temperature,
        double feelsLike,
        String description,
        String icon,
        double windSpeed,
        int humidity
) {
}
