import { Injectable, inject, signal } from '@angular/core';
import { TourLogApiService } from './tour-log-api.service';
import type { TourLog } from '../models/tour-log.model';

@Injectable({ providedIn: 'root' })
export class TourLogStateService {
  private readonly tourLogApi = inject(TourLogApiService);

  private readonly _logs = signal<TourLog[]>([]);
  private readonly _loading = signal(false);
  private readonly _loadError = signal<string | null>(null);

  readonly logs = this._logs.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly loadError = this._loadError.asReadonly();

  loadLogsForTour(tourId: number): void {
    this._loading.set(true);
    this._loadError.set(null);

    this.tourLogApi.getByTourId(tourId).subscribe({
      next: (data) => {
        this._logs.set(data);
        this._loading.set(false);
      },

      error: (err) => {
        this._loadError.set('Hoppla! Da ist beim Laden etwas schiefgegangen.');
        this._loading.set(false);
      }
    });
  }

  addLog(log: TourLog): void {
    this._logs.update(currentLogs => [...currentLogs, log]);
  }

  updateLogInState(updated: TourLog): void {
    this._logs.update(logs => logs.map(l => l.id === updated.id ? updated : l));

  }
  removeLog(logId: number): void {
    this._logs.update(logs => logs.filter(l => l.id !== logId));
  }

  reset(): void {
    this._logs.set([]);
  }
}
