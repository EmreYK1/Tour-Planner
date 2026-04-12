// frontend/src/app/shared/button/button.component.ts
// Wiederverwendbare Button-Komponente – zentrale Stelle für alle Button-Stile der App.
// Varianten: primary, secondary, danger, edit, ghost

import { Component, HostBinding, Input } from '@angular/core';

export type ButtonVariant = 'primary' | 'secondary' | 'danger' | 'edit' | 'ghost';

@Component({
  selector: 'app-button',
  standalone: true,
  templateUrl: './button.component.html',
  styleUrl: './button.component.scss'
})
export class ButtonComponent {
  /** Visueller Stil des Buttons */
  @Input() variant: ButtonVariant = 'primary';

  /** HTML button type – 'submit' löst ngSubmit im Eltern-Formular aus */
  @Input() type: 'button' | 'submit' = 'button';

  /** Deaktiviert den Button – blockiert auch Klick-Events auf dem Host */
  @Input() disabled = false;

  // Verhindert Klicks auf das Host-Element, wenn disabled
  @HostBinding('style.pointer-events')
  get pointerEvents(): string | null {
    return this.disabled ? 'none' : null;
  }
}
