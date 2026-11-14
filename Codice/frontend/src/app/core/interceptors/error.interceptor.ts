import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';

/** Richieste di login/registrazione: un 401 qui significa "credenziali errate". */
export function isAuthRequest(url: string): boolean {
  return url.includes('/api/auth/login') || url.includes('/api/auth/register');
}

/**
 * Functional Interceptor per la gestione centralizzata degli errori.
 * Utilizza ToastService per mostrare notifiche all'utente.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 403) {
        // 403: mostra il motivo indicato dal backend, se presente
        toastService.error(error.error?.message || 'Non hai i permessi per eseguire questa azione');
      } else if (error.status === 401) {
        // Login/registrazione: il 401 indica credenziali errate e il form mostra già
        // il messaggio del backend. Altrove indica una sessione scaduta.
        if (!isAuthRequest(req.url)) {
          toastService.error('Sessione scaduta o non valida');
        }
      } else if (error.status === 0) {
        toastService.error('Impossibile connettersi al server');
      } else {
        const message = error.error?.message || 'Si è verificato un errore inaspettato';
        toastService.error(message);
      }
      
      return throwError(() => error);
    })
  );
};
