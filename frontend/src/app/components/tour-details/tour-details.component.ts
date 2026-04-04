import { Component, inject } from '@angular/core';
import { TourStateService } from '../../services/tour-state.service';

@Component({
  selector: 'app-tour-details',
  imports: [],
  templateUrl: './tour-details.component.html',
  styleUrl: './tour-details.component.scss'
})
export class TourDetailsComponent {
  private readonly tourState = inject(TourStateService);

  // Dieses Signal leiten wir jetzt direkt an das HTML weiter
  readonly selectedTour = this.tourState.selectedTour;

  // Hilfsfunktion zur Umrechnung von Sekunden in "Xh YYmin"
  formatDuration(seconds: number): string {
    if (!seconds) return '0h 00min';
    const hours = Math.floor(seconds / 3600); // 1 Stunde hat 3600 Sekunden
    const minutes = Math.floor((seconds % 3600) / 60);

    // Damit Minuten wie "5" als "05" angezeigt werden, nutzen wir padStart
    const formattedMinutes = minutes.toString().padStart(2, '0');
    return `${hours}h ${formattedMinutes}min`;
  }
}

