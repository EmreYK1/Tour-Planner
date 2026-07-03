// frontend/src/app/components/tour-form/tour-form.component.ts
// Formular-Komponente für Anlegen und Bearbeiten von Touren – ein und dieselbe Komponente für beides (DRY).

import { Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { TourApiService } from '../../services/tour-api.service';
import { TourStateService } from '../../services/tour-state.service';
import { TourUiStateService } from '../../services/tour-ui-state.service';
import { TRANSPORT_TYPES, TRANSPORT_LABELS, type Tour } from '../../models/tour.model';
import { ButtonComponent } from '../../shared/button/button.component';
import { GeocodingService, GeocodingResult } from '../../services/geocoding.service';
import { forkJoin } from 'rxjs';

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
  private readonly geocodingService = inject(GeocodingService);

  // Ob wir gerade eine bestehende Tour bearbeiten oder eine neue anlegen (null = neu)
  readonly tourToEdit = this.tourUiState.tourToEdit;

  // Für das Dropdown im Template – alle verfügbaren Transportmittel
  readonly transportTypes = TRANSPORT_TYPES;

  // Deutsche Labels für die Enum-Werte aus dem Backend
  readonly transportLabels = TRANSPORT_LABELS;

  selectedFile: File | null = null;
  previewUrl: string | null = null;
  isGeocoding = false;
  geocodingError: string | null = null;

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

  private uploadIfSelected(tour: Tour, onDone: (t: Tour) => void): void {
    if (!this.selectedFile || !tour.id) { onDone(tour); return; }

    this.tourApi.uploadImage(tour.id, this.selectedFile).subscribe({
      next: (imageUrl) => 
        onDone({ ...tour, image: imageUrl }),
      error: (err) => {
        alert('Bild konnte nicht hochgeladen werden. Nur JPEG, PNG und WebP sind erlaubt.');
    }
    });
  }

  clearFile(fileInput: HTMLInputElement): void {
    fileInput.value = '';
    this.selectedFile = null;
    this.previewUrl = null;
  }

  // Entscheidet anhand von tourToEdit ob wir erstellen oder aktualisieren
  onSubmit(): void {
    if (!this.tourForm.valid) return;

        // Dateityp vorab prüfen
        const allowed = ['image/jpeg', 'image/png', 'image/webp'];
        if (this.selectedFile && !allowed.includes(this.selectedFile.type)) {
            alert('Ungültiger Dateityp. Nur JPEG, PNG und WebP sind erlaubt.');
            return;
        }

    this.isGeocoding = true;  
    this.geocodingError = null;

    forkJoin({
      from: this.geocodingService.fetchGeocode(this.tourForm.getRawValue().from),
      to:   this.geocodingService.fetchGeocode(this.tourForm.getRawValue().to),
    }).subscribe({
      next: ({ from, to }) => {
        this.isGeocoding = false;
        const tour = this.tourUiState.tourToEdit();
        const tourData = this.buildTourData(tour, from, to);

        if (tour?.id) {
          this.tourApi.update(tour.id, tourData).subscribe({
            next: (updatedTour) => {
              this.uploadIfSelected(updatedTour, (t) => this.tourState.updateTourInState(t));
              this.handleSuccess();
            },
            error: (err) => this.handleError('aktualisieren', err)
          });
        } else {
          this.tourApi.create(tourData).subscribe({
            next: (createdTour) => {
              this.uploadIfSelected(createdTour, (t) => this.tourState.addTour(t));
              this.handleSuccess();
            },
            error: (err) => this.handleError('erstellen', err)
          });
        }
      },
      error: () => {
        // Geocoding fehlgeschlagen – Adresse wurde von ORS nicht gefunden
        this.isGeocoding = false;
        this.geocodingError = 'Adresse nicht gefunden. Bitte überprüfe Start und Ziel.';
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
      this.previewUrl = URL.createObjectURL(this.selectedFile);
    }
  }

  // Schließt das Formular ohne zu speichern
  onCancel(): void {
    this.tourUiState.closeForm();
  }

  // Baut ein fertiges Tour-Objekt aus den Formulardaten und den Geocoding-Koordinaten zusammen
  private buildTourData(existing: Tour | null, fromCoords?: GeocodingResult, toCoords?: GeocodingResult): Tour {
    const rawForm = this.tourForm.getRawValue();
    return {
      ...rawForm,
      estimatedTime: rawForm.estimatedTime * 3600,
      id: existing?.id ?? null,
      fromLon: fromCoords?.lon ?? null,
      fromLat: fromCoords?.lat ?? null,
      toLon:   toCoords?.lon ?? null,
      toLat:   toCoords?.lat ?? null,
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
