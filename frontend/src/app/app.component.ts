// frontend/src/app/app.component.ts
// Root-Komponente der Angular-App (Shell).
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { TourStateService } from './services/tour-state.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'Tour Planner';
  private readonly tourState = inject(TourStateService);

  readonly tours = this.tourState.tours;
  readonly loadError = this.tourState.loadError;
  readonly loading = this.tourState.loading;

  ngOnInit(): void {
    this.tourState.loadTours();
  }
}
