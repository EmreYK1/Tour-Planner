// frontend/src/app/components/tour-form/tour-form.component.ts
// Formular-Komponente für Anlegen und Bearbeiten von Touren – ein und dieselbe Komponente für beides (DRY).

import { Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { TourApiService } from '../../services/tour-api.service';
import { TourStateService } from '../../services/tour-state.service';
import { TourUiStateService } from '../../services/tour-ui-state.service';
import { TRANSPORT_TYPES, TRANSPORT_LABELS, type Tour } from '../../models/tour.model';
import { ButtonComponent } from '../../shared/button/button.component';

@Component({
  selector: 'app-tour-form',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonComponent],
  templateUrl: './tour-form.component.html',
  styleUrl: './tour-form.component.scss'
})
export class TourFormComponent implements OnInit {
  private readonly tourApi = inject(TourApiService);
  private readonly tourState = inject(TourStateService);
  private readonly tourUiState = inject(TourUiStateService);

  // Ob wir gerade eine bestehende Tour bearbeiten oder eine neue anlegen (null = neu)
  readonly tourToEdit = this.tourUiState.tourToEdit;

  // Für das Dropdown im Template – alle verfügbaren Transportmittel
  readonly transportTypes = TRANSPORT_TYPES;

  // Deutsche Labels für die Enum-Werte aus dem Backend
  readonly transportLabels = TRANSPORT_LABELS;

  // Das Reactive Form mit allen Feldern und Validierungsregeln
  tourForm = new FormGroup({
    name: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    }),
    description: new FormControl('', { nonNullable: true }),
    from: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    to: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    transportType: new FormControl<typeof TRANSPORT_TYPES[number]>('BICYCLE', { nonNullable: true, validators: [Validators.required] }),
    distance: new FormControl(0, { nonNullable: true, validators: [Validators.min(0)] }),
    estimatedTime: new FormControl(0, { nonNullable: true, validators: [Validators.min(0)] }),
    image: new FormControl('', { nonNullable: true })
  });

  // Wenn eine Tour bearbeitet wird, Felder mit deren Werten vorausfüllen
  ngOnInit(): void {
    const tour = this.tourUiState.tourToEdit();
    if (tour) {
      this.tourForm.patchValue({
        ...tour,
        estimatedTime: tour.estimatedTime / 3600
      });
    }
  }

  // Entscheidet anhand von tourToEdit ob wir erstellen oder aktualisieren
  onSubmit(): void {
    if (!this.tourForm.valid) return;

    const tour = this.tourUiState.tourToEdit();
    const tourData = this.buildTourData(tour);

    if (tour?.id) {
      this.tourApi.update(tour.id, tourData).subscribe({
        next: (updatedTour) => {
          this.tourState.updateTourInState(updatedTour);
          this.handleSuccess();
        },
        error: (err) => this.handleError('aktualisieren', err)
      });
    } else {
      this.tourApi.create(tourData).subscribe({
        next: (createdTour) => {
          this.tourState.addTour(createdTour);
          this.handleSuccess();
        },
        error: (err) => this.handleError('erstellen', err)
      });
    }
  }

  // Schließt das Formular ohne zu speichern
  onCancel(): void {
    this.tourUiState.closeForm();
  }

  // Baut ein fertiges Tour-Objekt aus den Formulardaten und der bestehenden Tour zusammen
  private buildTourData(existing: Tour | null): Tour {
    const rawForm = this.tourForm.getRawValue();
    return {
      ...rawForm,
      estimatedTime: rawForm.estimatedTime * 3600,
      id: existing?.id ?? null
    };
  }

  // Schließt das Formular und setzt alle Felder auf die Standardwerte zurück
  private handleSuccess(): void {
    this.tourUiState.closeForm();
    this.tourForm.reset({ transportType: 'BICYCLE', distance: 0, estimatedTime: 0 });
  }

  // Zeigt einen Fehler in der Konsole und informiert den Nutzer per Alert
  private handleError(action: string, error: unknown): void {
    console.error(`Fehler beim ${action} der Tour:`, error);
    alert('Die Tour konnte leider nicht gespeichert werden.');
  }
}
