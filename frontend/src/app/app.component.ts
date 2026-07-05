// frontend/src/app/app.component.ts
// Root-Shell der App: enthält nur das router-outlet, damit Router.navigate() tatsächlich
// zwischen Login/Register und der geschützten Hauptansicht wechseln kann.

import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {}
