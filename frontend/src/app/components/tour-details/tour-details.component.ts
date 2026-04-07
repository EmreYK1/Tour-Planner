// frontend/src/app/components/tour-details/tour-details.component.ts
// Zeigt alle Details der aktuell ausgewählten Tour an und bietet einen Edit-Button.

import { Component, inject } from '@angular/core';
import { TourStateService } from '../../services/tour-state.service';
<<<<<<< HEAD
import { TourUiStateService } from '../../services/tour-ui-state.service';
=======
import { TourApiService } from '../../services/tour-api.service';
>>>>>>> 94b8e35 (Delete Funktion angelegt)

@Component({
  selector: 'app-tour-details',
  imports: [],
  templateUrl: './tour-details.component.html',
  styleUrl: './tour-details.component.scss'
})
export class TourDetailsComponent {
  private readonly tourState = inject(TourStateService);
<<<<<<< HEAD
  private readonly tourUiState = inject(TourUiStateService);
=======
  private readonly tourApi = inject(TourApiService);
>>>>>>> 94b8e35 (Delete Funktion angelegt)

  // Die aktuell ausgewählte Tour – null wenn noch nichts angeklickt wurde
  readonly selectedTour = this.tourState.selectedTour;

  // Rechnet Sekunden in ein lesbares Format um, z.B. 3661 → "1h 01min"
  formatDuration(seconds: number): string {
    if (!seconds) return '0h 00min';
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    return `${hours}h ${minutes.toString().padStart(2, '0')}min`;
  }

  // Öffnet das Formular im Bearbeiten-Modus mit der übergebenen Tour vorausgefüllt
  onEdit(tour: any): void {
    this.tourUiState.openEditForm(tour);
  }

  deleteTour(): void {
    const tour = this.selectedTour();

    if(!tour || tour.id == null) {
      return;
    }

    const id = tour.id;
    if (!confirm('Möchtest du diese Tour wirklich löschen?')) return;

    this.tourApi.delete(id).subscribe({
    next: () => this.tourState.removeTourFromState(id),
    error: () => alert('Löschen fehlgeschlagen. Bitte versuche es erneut.')
    });
 }

}
