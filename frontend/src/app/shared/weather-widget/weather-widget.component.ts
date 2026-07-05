// frontend/src/app/shared/weather-widget/weather-widget.component.ts
// Zeigt das aktuelle Wetter am Startpunkt einer Tour an (Unique Feature: OpenWeatherMap-Integration).

import { Component, Input, OnChanges, SimpleChanges, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { WeatherService } from '../../services/weather.service';
import type { Weather } from '../../models/weather.model';

@Component({
  selector: 'app-weather-widget',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './weather-widget.component.html',
  styleUrl: './weather-widget.component.scss'
})
export class WeatherWidgetComponent implements OnChanges {
  private readonly weatherService = inject(WeatherService);

  @Input() tourId: number | null = null;

  weather: Weather | null = null;
  loading = false;
  errorMessage: string | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['tourId']) {
      this.loadWeather();
    }
  }

  private loadWeather(): void {
    this.weather = null;
    this.errorMessage = null;

    if (this.tourId == null) {
      return;
    }

    this.loading = true;
    this.weatherService.getWeatherForTour(this.tourId).subscribe({
      next: (data) => {
        this.weather = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Wetterdaten konnten nicht geladen werden.';
        this.loading = false;
      }
    });
  }

  iconUrl(icon: string): string {
    return `https://openweathermap.org/img/wn/${icon}@2x.png`;
  }
}
