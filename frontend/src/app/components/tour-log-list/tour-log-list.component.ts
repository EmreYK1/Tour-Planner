import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TourLogStateService } from '../../services/tour-log-state.service';
import { formatDuration } from '../../utils/format-duration.util';

@Component({
  selector: 'app-tour-log-list',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './tour-log-list.component.html',
  styleUrl: './tour-log-list.component.scss',
})
export class TourLogListComponent {
  private readonly tourLogState = inject(TourLogStateService);

  readonly logs = this.tourLogState.logs;
  readonly loading = this.tourLogState.loading;
  readonly loadError = this.tourLogState.loadError;

  readonly formatDuration = formatDuration;
}
