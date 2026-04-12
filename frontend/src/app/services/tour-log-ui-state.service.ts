// frontend/src/app/services/tour-log-ui-state.service.ts
import { Injectable, signal } from '@angular/core';
import type { TourLog } from '../models/tour-log.model';

@Injectable({ providedIn: 'root' })
export class TourLogUiStateService {
  private readonly _showLogForm = signal(false);
  private readonly _logToEdit = signal<TourLog | null>(null);

  readonly showLogForm = this._showLogForm.asReadonly();
  readonly logToEdit = this._logToEdit.asReadonly();

  openCreateLogForm(): void {
    this._logToEdit.set(null);
    this._showLogForm.set(true);
  }

  openEditLogForm(log: TourLog): void {
    this._logToEdit.set(log);
    this._showLogForm.set(true);
  }

  closeLogForm(): void {
    this._showLogForm.set(false);
    this._logToEdit.set(null);
  }
}