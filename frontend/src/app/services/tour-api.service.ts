// frontend/src/app/services/tour-api.service.ts
// HTTP-Zugriff auf die Tour-REST-API des Spring-Boot-Backends.

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import type { Tour } from '../models/tour.model';

@Injectable({ providedIn: 'root' })
export class TourApiService {
  private readonly http = inject(HttpClient);
  private readonly resourceUrl = '/api/tours';

  getAll(): Observable<Tour[]> {
    return this.http.get<Tour[]>(this.resourceUrl);
  }

  getById(id: number): Observable<Tour> {
    return this.http.get<Tour>(`${this.resourceUrl}/${id}`);
  }

  create(tour: Tour): Observable<Tour> {
    return this.http.post<Tour>(this.resourceUrl, tour);
  }

  update(id: number, tour: Tour): Observable<Tour> {
    return this.http.put<Tour>(`${this.resourceUrl}/${id}`, tour);
  }
}

