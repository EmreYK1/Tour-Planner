// frontend/src/app/app.component.ts
// App-Hauptkomponente.

import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';

import { TourStateService } from './services/tour-state.service';
import { TourListComponent } from './components/tour-list/tour-list.component';
import { TourDetailsComponent } from './components/tour-details/tour-details.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [TourDetailsComponent, CommonModule, TourListComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'Tour Planner';

  // Holt den zentralen State-Service rein
  private readonly tourState = inject(TourStateService);

  // Lädt die Touren beim Start der Komponente einmalig vom Backend
  ngOnInit(): void {
    this.tourState.loadTours();
  }
}
