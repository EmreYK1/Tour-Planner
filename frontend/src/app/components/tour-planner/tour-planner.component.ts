// frontend/src/app/components/tour-planner/tour-planner.component.ts
// Geschützte Hauptansicht (Master-Detail-Layout) – wird nur nach erfolgreichem Login via Router angezeigt.

import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';

import { TourStateService } from '../../services/tour-state.service';
import { TourUiStateService } from '../../services/tour-ui-state.service';
import { TourLogUiStateService } from '../../services/tour-log-ui-state.service';
import { TourListComponent } from '../tour-list/tour-list.component';
import { TourDetailsComponent } from '../tour-details/tour-details.component';
import { TourFormComponent } from '../tour-form/tour-form.component';
import { TourLogFormComponent } from '../tour-log-form/tour-log-form.component';

@Component({
  selector: 'app-tour-planner',
  standalone: true,
  imports: [TourDetailsComponent, CommonModule, TourListComponent, TourFormComponent, TourLogFormComponent],
  templateUrl: './tour-planner.component.html',
  styleUrl: './tour-planner.component.scss'
})
export class TourPlannerComponent implements OnInit {
  title = 'Tour Planner';

  private readonly tourState = inject(TourStateService);
  private readonly tourUiState = inject(TourUiStateService);
  private readonly tourLogUiState = inject(TourLogUiStateService);

  // Ob das Tour-Formular gerade sichtbar ist
  readonly showForm = this.tourUiState.showForm;

  // Ob das Log-Formular gerade sichtbar ist
  readonly showLogForm = this.tourLogUiState.showLogForm;

  // Welche Tour gerade bearbeitet wird – null bedeutet "neue Tour anlegen"
  readonly tourToEdit = this.tourUiState.tourToEdit;

  // Beim Betreten der Ansicht einmalig Touren vom Backend laden
  ngOnInit(): void {
    this.tourState.loadTours();
  }
}
