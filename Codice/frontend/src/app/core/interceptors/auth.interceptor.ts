import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Functional Interceptor per la gestione dell'autenticazione.
 * Aggiunge il token Bearer all'header Authorization delle richieste.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const token = localStorage.getItem('token');

  let request = req;
  if (token) {
    request = req.clone({
      headers: req.headers.set('Authorization', token)
    });
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        // Token scaduto o non autorizzato
        authService.clearAuth();
        router.navigate(['/auth/login']);
      }
      return throwError(() => error);
    })
  );
};
