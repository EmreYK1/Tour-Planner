// frontend/src/app/services/tour-state.service.ts
// Verwaltet die Touren-Daten: Liste, Ladezustand und die aktuell ausgewählte Tour.
// UI-State (Formular sichtbar, welche Tour wird bearbeitet) lebt im TourUiStateService.

import { Injectable, inject, signal } from '@angular/core';
import { TourApiService } from './tour-api.service';
import type { Tour } from '../models/tour.model';

@Injectable({ providedIn: 'root' })
export class TourStateService {
  private readonly tourApi = inject(TourApiService);

  // Interne Signals – nur dieser Service darf sie verändern
  private readonly _tours = signal<Tour[]>([]);
  private readonly _loading = signal(false);
  private readonly _loadError = signal<string | null>(null);
  private readonly _selectedTour = signal<Tour | null>(null);

  // Nach außen nur lesbar – Komponenten können keine Daten direkt überschreiben
  readonly tours = this._tours.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly loadError = this._loadError.asReadonly();
  readonly selectedTour = this._selectedTour.asReadonly();

  // Verhindert mehrfaches Laden beim Navigieren hin und her
  private hasLoaded = false;

  // Lädt alle Touren vom Backend – passiert nur einmal, solange die App läuft
  loadTours(): void {
    if (this.hasLoaded || this._loading()) {
      return;
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

  // Setzt die aktuell angezeigte Tour (null = nichts ausgewählt)
  selectTour(tour: Tour | null): void {
    this._selectedTour.set(tour);
  }

  // Entfernt eine Tour aus dem State und setzt die ausgewählte Tour auf null
  removeTourFromState(id: number): void {
    this._tours.update((list) => list.filter((t) => t.id !== id));
    this.selectTour(null);
  }

  // Fügt eine neue Tour lokal hinzu und wählt sie direkt aus
  addTour(tour: Tour): void {
    this._tours.update((list) => [...list, tour]);
    this.selectTour(tour);
  }

  // Tauscht eine Tour in der Liste aus (nach erfolgreichem Update vom Backend)
  updateTourInState(updatedTour: Tour): void {
    this._tours.update((list) => list.map((t) => (t.id === updatedTour.id ? updatedTour : t)));
    this.selectTour(updatedTour);
  }
}
