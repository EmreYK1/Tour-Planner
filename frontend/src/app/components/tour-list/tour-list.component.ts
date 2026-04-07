// frontend/src/app/components/tour-list/tour-list.component.ts
// Reine UI-Komponente für die Sidebar (macht keine eigenen API-Calls).

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TourStateService } from '../../services/tour-state.service';
import type { Tour } from '../../models/tour.model';

@Component({
  selector: 'app-tour-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tour-list.component.html',
  styleUrl: './tour-list.component.scss'
})
export class TourListComponent {
  // Service einbinden, um an die Datenstruktur zu kommen
  private readonly tourState = inject(TourStateService);

  // Variablen aus dem State fürs HTML-Template bereitstellen
  readonly tours = this.tourState.tours;
  readonly loading = this.tourState.loading;
  readonly loadError = this.tourState.loadError;
  readonly selectedTour = this.tourState.selectedTour;

  onTourClick(tour: Tour): void {
    this.tourState.selectTour(tour);
  }

  toggleForm(): void {
    this.tourState.openCreateForm();
  }
}
