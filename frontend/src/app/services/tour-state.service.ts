// frontend/src/app/services/tour-state.service.ts

import { Injectable, inject, signal } from '@angular/core';
import { TourApiService } from './tour-api.service';
import type { Tour } from '../models/tour.model';

@Injectable({ providedIn: 'root' })
export class TourStateService {
  private readonly tourApi = inject(TourApiService);

  private readonly _tours = signal<Tour[]>([]);
  private readonly _loading = signal(false);
  private readonly _loadError = signal<string | null>(null);
  private readonly _selectedTour = signal<Tour | null>(null);
  private readonly _searchQuery = signal<string>('');

  readonly tours = this._tours.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly loadError = this._loadError.asReadonly();
  readonly selectedTour = this._selectedTour.asReadonly();
  readonly searchQuery = this._searchQuery.asReadonly();

  private hasLoaded = false;

  // Lädt Touren vom Backend; mit query immer frisch, ohne query nur beim ersten Aufruf.
  loadTours(query?: string): void {
    const isSearch = query !== undefined;

    if (!isSearch && (this.hasLoaded || this._loading())) {
      return;
    }

    this._searchQuery.set(query ?? '');
    this._loading.set(true);
    this._loadError.set(null);

    this.tourApi.getAll(query).subscribe({
      next: (data) => {
        this._tours.set(data);
        this._loading.set(false);
        this.hasLoaded = !isSearch;
      },
      error: () => {
        this._loadError.set(
          'Backend nicht erreichbar. Zuerst starten: cd backend → mvn spring-boot:run -Dspring-boot.run.profiles=dev'
        );
        this._loading.set(false);
      }
    });
  }

  // Setzt die aktuell angezeigte Tour.
  selectTour(tour: Tour | null): void {
    this._selectedTour.set(tour);
  }

  // Entfernt eine Tour aus dem State und hebt die Auswahl auf.
  removeTourFromState(id: number): void {
    this._tours.update((list) => list.filter((t) => t.id !== id));
    this.selectTour(null);
  }

  // Fügt eine neue Tour hinzu und wählt sie direkt aus.
  addTour(tour: Tour): void {
    this._tours.update((list) => [...list, tour]);
    this.selectTour(tour);
  }

  // Tauscht eine Tour in der Liste aus und aktualisiert die Auswahl.
  updateTourInState(updatedTour: Tour): void {
    this._tours.update((list) => list.map((t) => (t.id === updatedTour.id ? updatedTour : t)));
    this.selectTour(updatedTour);
  }
}
