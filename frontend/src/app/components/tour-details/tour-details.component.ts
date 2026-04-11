// frontend/src/app/components/tour-details/tour-details.component.ts
// Zeigt alle Details der aktuell ausgewählten Tour an und bietet Edit/Löschen.

import { Component, effect, inject } from '@angular/core';
import type { Tour } from '../../models/tour.model';
import { TourApiService } from '../../services/tour-api.service';
import { TourStateService } from '../../services/tour-state.service';
import { TourUiStateService } from '../../services/tour-ui-state.service';
import { TourLogStateService } from '../../services/tour-log-state.service';
import { TourLogListComponent } from '../tour-log-list/tour-log-list.component';
import { formatDuration } from '../../utils/format-duration.util';

@Component({
  selector: 'app-tour-details',
  imports: [TourLogListComponent],
  templateUrl: './tour-details.component.html',
  styleUrl: './tour-details.component.scss'
})
export class TourDetailsComponent {
  private readonly tourState = inject(TourStateService);
  private readonly tourUiState = inject(TourUiStateService);
  private readonly tourApi = inject(TourApiService);
  private readonly tourLogState = inject(TourLogStateService);

  // Die aktuell ausgewählte Tour – null wenn noch nichts angeklickt wurde
  readonly selectedTour = this.tourState.selectedTour;

  constructor() {
    effect(() => {
      const tour = this.selectedTour();
      if (tour && tour.id) {
        this.tourLogState.loadLogsForTour(tour.id);
      } else {
        this.tourLogState.reset();
      }
    });
  }

  // Rechnet Sekunden in ein lesbares Format um, z.B. 3661 → "1h 01min"
  readonly formatDuration = formatDuration;

  // Öffnet das Formular im Bearbeiten-Modus mit der übergebenen Tour vorausgefüllt
  onEdit(tour: Tour): void {
    this.tourUiState.openEditForm(tour);
  }

  deleteTour(): void {
    const tour = this.selectedTour();
    if (!tour || tour.id == null) return;

    const id = tour.id;
    if (!confirm('Möchtest du diese Tour wirklich löschen?')) return;

    this.tourApi.delete(id).subscribe({
      next: () => this.tourState.removeTourFromState(id),
      error: () => alert('Löschen fehlgeschlagen. Bitte versuche es erneut.')
    });
  }
}
