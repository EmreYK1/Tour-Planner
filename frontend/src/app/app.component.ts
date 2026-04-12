// frontend/src/app/app.component.ts
// Die Root-Komponente – sie hält das Master-Detail-Layout zusammen und startet den Datenload.

import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';

import { TourStateService } from './services/tour-state.service';
import { TourUiStateService } from './services/tour-ui-state.service';
import { TourLogUiStateService } from './services/tour-log-ui-state.service';
import { TourListComponent } from './components/tour-list/tour-list.component';
import { TourDetailsComponent } from './components/tour-details/tour-details.component';
import { TourFormComponent } from './components/tour-form/tour-form.component';
import { TourLogFormComponent } from './components/tour-log-form/tour-log-form.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [TourDetailsComponent, CommonModule, TourListComponent, TourFormComponent, TourLogFormComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
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

  // Beim App-Start einmalig Touren vom Backend laden
  ngOnInit(): void {
    this.tourState.loadTours();
  }
}
