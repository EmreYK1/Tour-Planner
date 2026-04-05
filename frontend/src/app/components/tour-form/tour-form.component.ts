import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { TourApiService } from '../../services/tour-api.service';
import { TourStateService } from '../../services/tour-state.service';
import { TRANSPORT_TYPES } from '../../models/tour.model';

@Component({
  selector: 'app-tour-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './tour-form.component.html',
  styleUrl: './tour-form.component.scss'
})
export class TourFormComponent {
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


  onSubmit(): void {
    if (this.tourForm.valid) {
      const newTourData = this.tourForm.getRawValue();

      this.tourApi.create({
        ...newTourData,
        id: null,
        image: '' // Default image placeholder
      }).subscribe({
        next: (createdTour) => {
          // Wenn erfolgreich: Tour in den globalen State pushen
          this.tourState.addTour(createdTour);
          // Formular schließen
          this.tourState.toggleForm();
          // Formular leeren für die nächste Eingabe
          this.tourForm.reset({
            transportType: 'BICYCLE',
            distance: 0,
            estimatedTime: 0
          });
        },
        error: (err) => {
          console.error('Fehler beim Erstellen der Tour:', err);
          alert('Die Tour konnte leider nicht gespeichert werden.');
        }
      });
    }
  }

  onCancel(): void {
    this.tourState.toggleForm();
  }
}
