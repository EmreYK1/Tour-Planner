// frontend/src/app/services/geocoding.service.ts
// Wandelt Freitextadressen via Backend-Endpoint in geografische Koordinaten (lon/lat) um.
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface GeocodingResult {
    lon: number;
    lat:number;
}

@Injectable({ providedIn: 'root'})
export class GeocodingService {
    private readonly http = inject(HttpClient);
    private readonly resourceUrl = '/api/geocode';
    fetchGeocode(address: string): Observable<GeocodingResult> {
        return this.http.get<GeocodingResult>(this.resourceUrl, { params: { q: address } });
    }
}
