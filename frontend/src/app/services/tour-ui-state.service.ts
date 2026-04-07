// frontend/src/app/services/tour-ui-state.service.ts
// Verwaltet den UI-State des Formulars: ob es sichtbar ist und welche Tour gerade bearbeitet wird.
// Bewusst vom TourStateService getrennt – jeder Service hat eine einzige Aufgabe (SRP).

import { Injectable, signal } from '@angular/core';
import type { Tour } from '../models/tour.model';

@Injectable({ providedIn: 'root' })
export class TourUiStateService {
  // Interne Signals – nur dieser Service schreibt darauf
  private readonly _showForm = signal(false);
  private readonly _tourToEdit = signal<Tour | null>(null);

  // Nach außen nur lesbar
  readonly showForm = this._showForm.asReadonly();
  readonly tourToEdit = this._tourToEdit.asReadonly();

  // Öffnet das Formular im Anlegen-Modus (kein tourToEdit → leeres Formular)
  openCreateForm(): void {
    this._tourToEdit.set(null);
    this._showForm.set(true);
  }

  // Öffnet das Formular im Bearbeiten-Modus und füllt es mit der übergebenen Tour vor
  openEditForm(tour: Tour): void {
    this._tourToEdit.set(tour);
    this._showForm.set(true);
  }

  // Schließt das Formular und setzt den Edit-Zustand zurück
  closeForm(): void {
    this._showForm.set(false);
    this._tourToEdit.set(null);
  }
}
