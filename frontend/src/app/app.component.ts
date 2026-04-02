// frontend/src/app/app.component.ts
// Root-Komponente der Angular-App (Shell).
import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import type { Tour } from './models/tour.model';
import { TourApiService } from './services/tour-api.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'Tour Planner';
  private readonly tourApi = inject(TourApiService);

  readonly tours = signal<Tour[]>([]);
  readonly loadError = signal<string | null>(null);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.tourApi.getAll().subscribe({
      next: (data) => {
        this.tours.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loadError.set(
          'Backend nicht erreichbar unter Port 8080. Zuerst starten: cd backend → mvn spring-boot:run -Dspring-boot.run.profiles=dev (ohne Postgres), dann: npm start.'
        );
        this.loading.set(false);
      }
    });
  }
}
