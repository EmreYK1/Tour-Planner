// frontend/src/app/components/tour-list/tour-list.component.ts

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TourStateService } from '../../services/tour-state.service';
import { TourUiStateService } from '../../services/tour-ui-state.service';
import { TourApiService } from '../../services/tour-api.service';
import { ToastService } from '../../services/toast.service';
import { ImportExportService } from '../../services/import-export.service';
import { AuthService } from '../../services/auth.service';
import { Tour, TRANSPORT_LABELS } from '../../models/tour.model';

@Component({
  selector: 'app-tour-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './tour-list.component.html',
  styleUrl: './tour-list.component.scss'
})
export class TourListComponent {
  private readonly tourState = inject(TourStateService);
  private readonly tourUiState = inject(TourUiStateService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly tourApi = inject(TourApiService);
  private readonly toastService = inject(ToastService);
  private readonly importExportService = inject(ImportExportService);

  readonly tours = this.tourState.tours;
  readonly loading = this.tourState.loading;
  readonly loadError = this.tourState.loadError;
  readonly selectedTour = this.tourState.selectedTour;
  readonly searchQuery = this.tourState.searchQuery;
  readonly transportLabels = TRANSPORT_LABELS;

  readonly searchControl = new FormControl('', { nonNullable: true });

  constructor() {
    // debounceTime(300) + distinctUntilChanged() verhindern unnötige API-Calls beim Tippen.
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntilDestroyed()
    ).subscribe((query) => {
      query.trim() === ''
        ? this.tourState.loadTours()
        : this.tourState.loadTours(query.trim());
    });
  }

  // Schließt offene Formulare und wählt die angeklickte Tour aus.
  onTourClick(tour: Tour): void {
    this.tourUiState.closeForm();
    this.tourState.selectTour(tour);
  }

  // Öffnet das Formular zum Anlegen einer neuen Tour.
  openCreateForm(): void {
    this.tourUiState.openCreateForm();
  }

  // Meldet den Nutzer ab und navigiert zur Login-Seite.
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Exportiert alle Touren als JSON-Datei.
  exportTours(): void {
    this.tourApi.exportTours().subscribe({
      next: (data) => {
        this.importExportService.downloadJson(data, 'tour-planner-export.json');
        this.toastService.showSuccess('Touren erfolgreich exportiert.');
      },
      error: () => {
        this.toastService.showError('Fehler beim Exportieren der Touren.');
      }
    });
  }

  // Verarbeitet die ausgewählte JSON-Datei für den Import.
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    this.importExportService.parseJsonFile(input.files[0]).subscribe({
      next: (importData) => {
        if (!Array.isArray(importData)) {
          this.toastService.showError('Ungültiges Dateiformat. Erwartet wird eine Liste von Touren.');
          return;
        }

        this.tourState.importTours(
          importData,
          () => this.toastService.showSuccess('Touren erfolgreich importiert.'),
          (err) => this.toastService.showError(`Import fehlgeschlagen: ${err}`)
        );
      },
      error: (err) => {
        this.toastService.showError(err.message);
      },
      complete: () => {
        // Input zurücksetzen, um dieselbe Datei erneut auswählen zu können
        input.value = '';
      }
    });
  }
}
