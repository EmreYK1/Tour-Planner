// frontend/src/app/services/tour-state.service.ts
// Zentrales State-Management für die Touren (Single Source of Truth).

import { Injectable, inject, signal } from '@angular/core';
import { TourApiService } from './tour-api.service';
import type { Tour } from '../models/tour.model';

@Injectable({ providedIn: 'root' })
export class TourStateService {
  private readonly tourApi = inject(TourApiService);

  // Private Signals: Nur dieser Service darf Werte mit .set() oder .update() verändern
  private readonly _tours = signal<Tour[]>([]);
  private readonly _loading = signal(false);
  private readonly _loadError = signal<string | null>(null);

  // Öffentliche Schnittstelle: Striktes "Read-Only". Komponenten können nur lesen!
  readonly tours = this._tours.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly loadError = this._loadError.asReadonly();

  private hasLoaded = false;

  loadTours(): void {
    if (this.hasLoaded || this._loading()) {
      return; // Bereits geladen oder Lädt gerade
    }

    this._loading.set(true);
    this._loadError.set(null);
    
    this.tourApi.getAll().subscribe({
      next: (data) => {
        this._tours.set(data);
        this._loading.set(false);
        this.hasLoaded = true;
      },
      error: () => {
        this._loadError.set(
          'Backend nicht erreichbar unter Port 8080. Zuerst starten: cd backend → mvn spring-boot:run -Dspring-boot.run.profiles=dev (ohne Postgres), dann: npm start.'
        );
        this._loading.set(false);
      }
    });
  }
}
