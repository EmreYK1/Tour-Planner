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

}
