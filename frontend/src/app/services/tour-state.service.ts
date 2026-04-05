// frontend/src/app/services/tour-state.service.ts
// Zentraler State-Service für die Touren (unser "Speicher").

import { Injectable, inject, signal } from '@angular/core';
import { TourApiService } from './tour-api.service';
import type { Tour } from '../models/tour.model';

@Injectable({ providedIn: 'root' })
export class TourStateService {
  private readonly tourApi = inject(TourApiService);

  // Interne Daten (dürfen nur hier geändert werden)
  private readonly _tours = signal<Tour[]>([]);
  private readonly _loading = signal(false);
  private readonly _loadError = signal<string | null>(null);
  private readonly _selectedTour = signal<Tour | null>(null);
  private readonly _showForm = signal(false)

  // Nach außen hin Read-Only machen, damit niemand aus Versehen Daten überschreibt
  readonly tours = this._tours.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly loadError = this._loadError.asReadonly();
  readonly selectedTour = this._selectedTour.asReadonly();
  readonly showForm = this._showForm.asReadonly();

  private hasLoaded = false;

  // Holt die Touren nur dann vom Backend, wenn sie noch nicht geladen wurden
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
  // Eine Methode, um das Formular zu öffnen/schließen 
  toggleForm(): void {
    this._showForm.update(val => !val);
  }

  selectTour(tour: Tour | null): void {
    this._selectedTour.set(tour);
  }

  addTour(tour: Tour): void {
    // Wir nehmen die alte Liste und hängen die neue Tour hinten an
    this._tours.update(list => [...list, tour]);

    // Danach schließen wir das Formular automatisch
    this._showForm.set(false);

    // Wir wählen die neue Tour direkt aus, damit sie in der Liste hervorgehoben wird
    this.selectTour(tour);


  }

}

