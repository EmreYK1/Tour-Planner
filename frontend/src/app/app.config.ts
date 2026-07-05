// frontend/src/app/app.config.ts
// Zentrale Angular-Konfiguration: hier werden alle globalen Provider registriert.

import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { jwtInterceptor } from './interceptors/jwt.interceptor';
import { authErrorInterceptor } from './interceptors/auth-error.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // Fasst mehrere Change-Detection-Events zusammen, damit die App nicht unnötig oft re-rendert
    provideZoneChangeDetection({ eventCoalescing: true }),
    // Macht HttpClient überall per inject() verfügbar; jwtInterceptor hängt den Token an,
    // authErrorInterceptor loggt bei 401/403 automatisch aus und leitet zum Login um
    provideHttpClient(withInterceptors([jwtInterceptor, authErrorInterceptor])),
    provideRouter(routes)
  ]
};
