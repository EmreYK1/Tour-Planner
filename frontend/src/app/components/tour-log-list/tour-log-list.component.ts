import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TourLogStateService } from '../../services/tour-log-state.service';
import { TourLogApiService } from '../../services/tour-log-api.service';
import { TourLogUiStateService } from '../../services/tour-log-ui-state.service';
import { formatDuration } from '../../utils/format-duration.util';
import { TourStateService } from '../../services/tour-state.service';
import type { TourLog } from '../../models/tour-log.model';
import { ButtonComponent } from '../../shared/button/button.component';

@Component({
  selector: 'app-tour-log-list',
  standalone: true,
  imports: [DatePipe, ButtonComponent],
  templateUrl: './tour-log-list.component.html',
  styleUrl: './tour-log-list.component.scss',
})
export class TourLogListComponent {
  private readonly tourLogState = inject(TourLogStateService);
  private readonly tourLogApi = inject(TourLogApiService);
  private readonly tourLogUiState = inject(TourLogUiStateService);
  private readonly tourState = inject(TourStateService);

  readonly logs = this.tourLogState.logs;
  readonly loading = this.tourLogState.loading;
  readonly loadError = this.tourLogState.loadError;

  readonly formatDuration = formatDuration;

  openCreateForm(): void {
    this.tourLogUiState.openCreateLogForm();
  }

  openEditForm(log: TourLog): void {
    this.tourLogUiState.openEditLogForm(log);
  }

  deleteLog(log: TourLog): void {
    const tour = this.tourState.selectedTour();
    if (!tour?.id || !log.id) return;

    this.tourLogApi.delete(tour.id, log.id).subscribe({
      next: () => this.tourLogState.removeLog(log.id!)
    });
  }
}
