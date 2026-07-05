import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

// Endpunkte, bei denen ein 401 eine normale, erwartbare Antwort ist (z.B. falsche Login-Daten)
// und daher KEIN automatisches Logout auslösen soll.
const AUTH_ENDPOINTS = ['/api/auth/'];

export const authErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isAuthEndpoint = AUTH_ENDPOINTS.some(endpoint => req.url.includes(endpoint));

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 401 = Token fehlt/ungültig, 403 = vom SecurityConfig-EntryPoint bei fehlender Authentifizierung
      if (!isAuthEndpoint && (error.status === 401 || error.status === 403)) {
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
