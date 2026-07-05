// frontend/src/app/services/tour-api.service.ts

import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import type { Tour } from '../models/tour.model';

@Injectable({ providedIn: 'root' })
export class TourApiService {
  private readonly http = inject(HttpClient);
  private readonly resourceUrl = '/api/tours';

  // Holt alle Touren, optional gefiltert nach Suchbegriff.
  getAll(search?: string): Observable<Tour[]> {
    const params = search?.trim()
      ? new HttpParams().set('search', search.trim())
      : undefined;
    return this.http.get<Tour[]>(this.resourceUrl, { params });
  }

  // Holt eine einzelne Tour anhand ihrer ID.
  getById(id: number): Observable<Tour> {
    return this.http.get<Tour>(`${this.resourceUrl}/${id}`);
  }

  // Legt eine neue Tour an.
  create(tour: Tour): Observable<Tour> {
    return this.http.post<Tour>(this.resourceUrl, tour);
  }

  // Aktualisiert eine bestehende Tour.
  update(id: number, tour: Tour): Observable<Tour> {
    return this.http.put<Tour>(`${this.resourceUrl}/${id}`, tour);
  }

  // Lädt ein Bild für eine Tour hoch.
  uploadImage(id: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.resourceUrl}/${id}/image`, formData, { responseType: 'text' });
  }

  // Löscht eine Tour anhand ihrer ID.
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/${id}`);
  }

  // Exportiert alle eigenen Touren inkl. Logs als JSON.
  exportTours(): Observable<any[]> {
    return this.http.get<any[]>(`${this.resourceUrl}/export`);
  }

  // Importiert Touren aus einer Liste.
  importTours(tours: any[]): Observable<void> {
    return this.http.post<void>(`${this.resourceUrl}/import`, tours);
  }
}
