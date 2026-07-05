// frontend/src/app/app.component.ts
// Root-Shell der App: enthält nur das router-outlet, damit Router.navigate() tatsächlich
// zwischen Login/Register und der geschützten Hauptansicht wechseln kann.

import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastService } from './services/toast.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  private readonly toastService = inject(ToastService);

  readonly toasts = this.toastService.toasts;

  dismissToast(id: string): void {
    this.toastService.dismiss(id);
  }
}
