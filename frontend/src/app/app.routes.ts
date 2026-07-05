// frontend/src/app/app.routes.ts
// Routendefinitionen: öffentliche Auth-Seiten + geschützte Hauptansicht via AuthGuard.

import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./components/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./components/tour-planner/tour-planner.component').then(m => m.TourPlannerComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
