// frontend/src/app/models/weather.model.ts
// Spiegelt das WeatherDto des Backends (aktuelles Wetter am Tour-Startpunkt).

export interface Weather {
  locationName: string;
  temperature: number;
  feelsLike: number;
  description: string;
  icon: string;
  windSpeed: number;
  humidity: number;
}
