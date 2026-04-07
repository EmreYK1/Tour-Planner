// frontend/src/app/services/tour-api.service.ts
// Kümmert sich ausschließlich um HTTP-Calls – kein State, keine Logik, nur Daten holen und schicken.

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import type { Tour } from '../models/tour.model';

@Injectable({ providedIn: 'root' })
export class TourApiService {
  private readonly http = inject(HttpClient);

  // Basis-URL zur Backend-API – der Proxy leitet /api in der Dev-Umgebung weiter
  private readonly resourceUrl = '/api/tours';

  // Holt alle Touren als Liste
  getAll(): Observable<Tour[]> {
    return this.http.get<Tour[]>(this.resourceUrl);
  }

  // Holt eine einzelne Tour anhand ihrer ID
  getById(id: number): Observable<Tour> {
    return this.http.get<Tour>(`${this.resourceUrl}/${id}`);
  }

  // Legt eine neue Tour an und gibt die gespeicherte Version (mit ID) zurück
  create(tour: Tour): Observable<Tour> {
    return this.http.post<Tour>(this.resourceUrl, tour);
  }


  // Aktualisiert eine bestehende Tour – das Backend gibt die aktualisierte Tour zurück
  update(id: number, tour: Tour): Observable<Tour> {
    return this.http.put<Tour>(`${this.resourceUrl}/${id}`, tour);
  }

  // Löscht eine Tour anhand ihrer ID
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/${id}`);
  }
}




