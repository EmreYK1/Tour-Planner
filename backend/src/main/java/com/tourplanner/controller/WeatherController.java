package com.tourplanner.controller;

import com.tourplanner.dto.WeatherDto;
import com.tourplanner.service.tour.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Liefert das aktuelle Wetter am Startpunkt einer Tour (OpenWeatherMap).
@RestController
@RequestMapping("/api/tours/{tourId}/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public WeatherDto get(@PathVariable long tourId) {
        return weatherService.getWeatherForTour(tourId);
    }
}
