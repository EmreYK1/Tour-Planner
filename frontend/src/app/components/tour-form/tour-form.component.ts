import { Component, inject, Input, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { TourApiService } from '../../services/tour-api.service';
import { TourStateService } from '../../services/tour-state.service';
import { TRANSPORT_TYPES, type Tour } from '../../models/tour.model';


@Component({
  selector: 'app-tour-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './tour-form.component.html',
  styleUrl: './tour-form.component.scss'
})
export class TourFormComponent implements OnInit {
  @Input() tourToEdit: Tour | null = null;

  // Sinn: Wir holen uns die Werkzeugkästen für API und State in unsere Komponente.
  private readonly tourApi = inject(TourApiService);
  private readonly tourState = inject(TourStateService);

  // Wir machen die Transporttypen im HTML verfügbar.
  readonly transportTypes = TRANSPORT_TYPES;

  // Mapping für schönere Labels im Dropdown
  readonly transportLabels: Record<typeof TRANSPORT_TYPES[number], string> = {
    WALK: 'Zu Fuß',
    BICYCLE: 'Fahrrad',
    CAR: 'Auto',
    PUBLIC_TRANSPORT: 'Öffentliche Verkehrsmittel'
  };

  // Das ist das digitale Abbild des Formulars inkl. Validierung.
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
    estimatedTime: new FormControl(0, { nonNullable: true, validators: [Validators.min(0)] })
  });

  ngOnInit(): void {
    if (this.tourToEdit) {
      // Hier werden die Werte der Tour in die Formularfelder kopiert
      this.tourForm.patchValue(this.tourToEdit);
    }
  }


  onSubmit(): void {
    if (this.tourForm.valid) {
      const rawValues = this.tourForm.getRawValue();

      // Wir bereiten die Daten vor
      const tourData: Tour = {
        ...rawValues,
        id: this.tourToEdit ? this.tourToEdit.id : null,
        image: this.tourToEdit ? this.tourToEdit.image : ''
      };

      if (this.tourToEdit && this.tourToEdit.id) {
        // --- MODUS: BEARBEITEN ---
        this.tourApi.update(this.tourToEdit.id, tourData).subscribe({
          next: (updatedTour) => {
            this.tourState.updateTourInState(updatedTour);
            this.handleSuccess();
          },
          error: (err) => this.handleError('aktualisieren', err)
        });
      } else {
        // --- MODUS: ANLEGEN ---
        this.tourApi.create(tourData).subscribe({
          next: (createdTour) => {
            this.tourState.addTour(createdTour);
            this.handleSuccess();
          },
          error: (err) => this.handleError('erstellen', err)
        });
      }
    }
  }

  // Hilfsmethode, um Duplikate im Code zu vermeiden (DRY!)
  private handleSuccess(): void {
    this.tourState.toggleForm();
    this.tourForm.reset({
      transportType: 'BICYCLE',
      distance: 0,
      estimatedTime: 0
    });
  }

  private handleError(action: string, error: any): void {
    console.error(`Fehler beim ${action} der Tour:`, error);
    alert('Die Tour konnte leider nicht gespeichert werden.');
  }

  onCancel(): void {
    this.tourState.toggleForm();
  }
}
