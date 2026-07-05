package com.tourplanner.service.tour;

import com.tourplanner.client.WeatherClient;
import com.tourplanner.dto.WeatherDto;
import com.tourplanner.exception.WeatherApiException;
import com.tourplanner.model.Tour;
import org.springframework.stereotype.Service;

// Liefert das aktuelle Wetter am Startpunkt einer (eigenen) Tour.
@Service
public class WeatherService {

    private final TourOwnershipGuard ownershipGuard;
    private final WeatherClient weatherClient;

    public WeatherService(TourOwnershipGuard ownershipGuard, WeatherClient weatherClient) {
        this.ownershipGuard = ownershipGuard;
        this.weatherClient = weatherClient;
    }

    public WeatherDto getWeatherForTour(long tourId) {
        Tour tour = ownershipGuard.requireOwnedTour(tourId);
        if (tour.getFromLat() == null || tour.getFromLon() == null) {
            throw new WeatherApiException(
                    "Für diese Tour sind keine Koordinaten hinterlegt. Bitte Tour einmal neu speichern.");
        }
        return weatherClient.fetchCurrentWeather(tour.getFromLat(), tour.getFromLon());
    }
}
