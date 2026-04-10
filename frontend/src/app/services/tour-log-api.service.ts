import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import type { TourLog } from '../models/tour-log.model';

@Injectable({ providedIn: 'root'})
export class TourLogApiService {
    private readonly http = inject(HttpClient);

    private url(tourId: number) {return `/api/tours/${tourId}/logs`; }

    getByTourId(tourId: number): Observable<TourLog[]> {
        return this.http.get<TourLog[]>(this.url(tourId));
    }

    create(tourId: number, tourLog: TourLog): Observable<TourLog> {
        return this.http.post<TourLog>(this.url(tourId), tourLog);
    }

    update(tourId: number, logId: number, tourLog: TourLog): Observable<TourLog> {
        return this.http.put<TourLog>(`${this.url(tourId)}/${logId}`, tourLog);
    }

    delete(tourId: number, logId: number): Observable<void> {
        return this.http.delete<void>(`${this.url(tourId)}/${logId}`);
    }               

    
}