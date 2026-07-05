// frontend/src/app/services/weather.service.ts
// Holt das aktuelle Wetter am Startpunkt einer Tour vom Backend (OpenWeatherMap).

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import type { Weather } from '../models/weather.model';

@Injectable({ providedIn: 'root' })
export class WeatherService {
  private readonly http = inject(HttpClient);

  getWeatherForTour(tourId: number): Observable<Weather> {
    return this.http.get<Weather>(`/api/tours/${tourId}/weather`);
  }
}
