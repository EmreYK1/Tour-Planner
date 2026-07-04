// frontend/src/app/app.config.ts
// Zentrale Angular-Konfiguration: hier werden alle globalen Provider registriert.

import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { jwtInterceptor } from './interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // Fasst mehrere Change-Detection-Events zusammen, damit die App nicht unnötig oft re-rendert
    provideZoneChangeDetection({ eventCoalescing: true }),
    // Macht HttpClient überall per inject() verfügbar und registriert den JWT-Interceptor
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideRouter(routes)
  ]
};
