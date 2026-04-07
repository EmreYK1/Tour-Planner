// frontend/src/app/app.component.ts
// Die Root-Komponente – sie hält das Master-Detail-Layout zusammen und startet den Datenload.

import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';

import { TourStateService } from './services/tour-state.service';
import { TourUiStateService } from './services/tour-ui-state.service';
import { TourListComponent } from './components/tour-list/tour-list.component';
import { TourDetailsComponent } from './components/tour-details/tour-details.component';
import { TourFormComponent } from './components/tour-form/tour-form.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [TourDetailsComponent, CommonModule, TourListComponent, TourFormComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'Tour Planner';

  private readonly tourState = inject(TourStateService);
  private readonly tourUiState = inject(TourUiStateService);

  // Ob das Formular gerade sichtbar ist (kommt aus dem UI-State-Service)
  readonly showForm = this.tourUiState.showForm;

  // Welche Tour gerade bearbeitet wird – null bedeutet "neue Tour anlegen"
  readonly tourToEdit = this.tourUiState.tourToEdit;

  // Beim App-Start einmalig Touren vom Backend laden
  ngOnInit(): void {
    this.tourState.loadTours();
  }
}
