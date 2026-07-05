package com.tourplanner.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tourplanner.dto.WeatherDto;
import com.tourplanner.exception.WeatherApiException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// Ruft die OpenWeatherMap Current-Weather-API ab und liefert das Ergebnis als WeatherDto.
@Service
public class WeatherClient {

    private static final String WEATHER_BASE_URL = "https://api.openweathermap.org";

    private final RestClient restClient;
    private final String apiKey;

    public WeatherClient(@Value("${openweather.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(WEATHER_BASE_URL)
                .build();
    }

    public WeatherDto fetchCurrentWeather(double lat, double lon) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new WeatherApiException("OpenWeatherMap API key is not configured (openweather.api.key)");
        }

        OpenWeatherResponse response = restClient.get()
                .uri("/data/2.5/weather?lat={lat}&lon={lon}&appid={key}&units=metric&lang=de", lat, lon, apiKey)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), (req, res) -> {
                    throw new WeatherApiException(
                            "OpenWeatherMap error " + res.getStatusCode().value());
                })
                .body(OpenWeatherResponse.class);

        return toDto(response);
    }

    private static WeatherDto toDto(OpenWeatherResponse response) {
        if (response == null || response.weather() == null || response.weather().isEmpty() || response.main() == null) {
            throw new WeatherApiException("OpenWeatherMap response is incomplete");
        }

        OpenWeatherResponse.Weather weather = response.weather().get(0);
        return new WeatherDto(
                response.name(),
                response.main().temp(),
                response.main().feelsLike(),
                weather.description(),
                weather.icon(),
                response.wind() != null ? response.wind().speed() : 0.0,
                response.main().humidity());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenWeatherResponse(
            List<Weather> weather,
            Main main,
            Wind wind,
            String name) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        private record Weather(String description, String icon) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        private record Main(
                double temp,
                @com.fasterxml.jackson.annotation.JsonProperty("feels_like") double feelsLike,
                int humidity) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        private record Wind(double speed) {}
    }
}
