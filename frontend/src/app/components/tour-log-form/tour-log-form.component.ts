// frontend/src/app/components/tour-log-form/tour-log-form.component.ts
// Formular-Komponente für das Anlegen (und später Bearbeiten) von Tour-Logs.
// Analog zu TourFormComponent – gleiche Struktur, eigener UI-State-Service (SRP).

import { Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { TourLogApiService } from '../../services/tour-log-api.service';
import { TourLogStateService } from '../../services/tour-log-state.service';
import { TourLogUiStateService } from '../../services/tour-log-ui-state.service';
import { TourStateService } from '../../services/tour-state.service';
import type { TourLog } from '../../models/tour-log.model';

@Component({
  selector: 'app-tour-log-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './tour-log-form.component.html',
  styleUrl: './tour-log-form.component.scss'
})
export class TourLogFormComponent implements OnInit {
  private readonly logApi = inject(TourLogApiService);
  private readonly logState = inject(TourLogStateService);
  private readonly logUiState = inject(TourLogUiStateService);
  private readonly tourState = inject(TourStateService);

  // Ob wir gerade einen bestehenden Log bearbeiten oder einen neuen anlegen (null = neu)
  readonly logToEdit = this.logUiState.logToEdit;

  // Die aktuell ausgewählte Tour – wir brauchen ihre ID für den API-Call
  private readonly selectedTour = this.tourState.selectedTour;

  // Das Reactive Form mit allen Feldern und Validierungsregeln
  logForm = new FormGroup({
    dateTime: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    }),
    comment: new FormControl('', {
      nonNullable: true
    }),
    difficulty: new FormControl(3, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1), Validators.max(5)]
    }),
    totalDistance: new FormControl(0, {
      nonNullable: true,
      validators: [Validators.min(0)]
    }),
    totalTime: new FormControl(0, {
      nonNullable: true,
      validators: [Validators.min(0)]
    }),
    rating: new FormControl(3, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1), Validators.max(5)]
    })
  });

  // Wenn ein Log bearbeitet wird, Felder mit dessen Werten vorausfüllen
  ngOnInit(): void {
    const log = this.logToEdit();
    if (log) {
      this.logForm.patchValue(log);
    }
  }

  // Entscheidet anhand von logToEdit ob wir erstellen oder aktualisieren
  onSubmit(): void {
    if (this.logForm.invalid) return;

    const tourId = this.selectedTour()?.id;
    if (!tourId) return;

    const logData = this.buildLogData(tourId);

    // Create-Modus (T3.3.1): Immer POST, da logToEdit null ist
    this.logApi.create(tourId, logData).subscribe({
      next: (createdLog) => {
        // Den neuen Log sofort im State (und damit in der Liste) anzeigen
        this.logState.addLog(createdLog);
        this.handleSuccess();
      },
      error: (err) => this.handleError('erstellen', err)
    });
  }

  // Schließt das Formular ohne zu speichern
  onCancel(): void {
    this.logUiState.closeLogForm();
  }

  // Baut ein fertiges TourLog-Objekt aus den Formulardaten zusammen (analog zu buildTourData)
  private buildLogData(tourId: number): TourLog {
    const existing = this.logToEdit();
    return {
      ...this.logForm.getRawValue(),
      id: existing?.id ?? null,
      tourId: tourId
    };
  }

  // Schließt das Formular und setzt alle Felder auf die Standardwerte zurück
  private handleSuccess(): void {
    this.logUiState.closeLogForm();
    this.logForm.reset({ difficulty: 3, rating: 3, totalDistance: 0, totalTime: 0 });
  }

  // Zeigt einen Fehler in der Konsole und informiert den Nutzer per Alert
  private handleError(action: string, error: unknown): void {
    console.error(`Fehler beim ${action} des Logs:`, error);
    alert('Der Log konnte leider nicht gespeichert werden.');
  }
}
