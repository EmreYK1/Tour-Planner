// frontend/src/app/components/tour-list/tour-list.component.ts
// Sidebar-Komponente: zeigt die Tourliste an und reagiert auf Klicks und Ladefehler.

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TourStateService } from '../../services/tour-state.service';
import { TourUiStateService } from '../../services/tour-ui-state.service';
import type { Tour } from '../../models/tour.model';

@Component({
  selector: 'app-tour-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tour-list.component.html',
  styleUrl: './tour-list.component.scss'
})
export class TourListComponent {
  private readonly tourState = inject(TourStateService);
  private readonly tourUiState = inject(TourUiStateService);

  // Signals direkt ans Template weitergeben – kein Kopieren, kein Umweg
  readonly tours = this.tourState.tours;
  readonly loading = this.tourState.loading;
  readonly loadError = this.tourState.loadError;
  readonly selectedTour = this.tourState.selectedTour;

  // Wird beim Klick auf ein Tour-Element in der Liste aufgerufen
  onTourClick(tour: Tour): void {
    this.tourState.selectTour(tour);
  }

  // Öffnet das leere Formular zum Anlegen einer neuen Tour
  openCreateForm(): void {
    this.tourUiState.openCreateForm();
  }
}
