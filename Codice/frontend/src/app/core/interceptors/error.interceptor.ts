import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';

/**
 * Functional Interceptor per la gestione centralizzata degli errori.
 * Utilizza ToastService per mostrare notifiche all'utente.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 403) {
        // Mostra toast per errore 403 Forbidden
        toastService.error('Non hai i permessi per eseguire questa azione');
      } else if (error.status === 401) {
        // Gestito da authInterceptor, ma aggiungiamo un messaggio se utile
        toastService.error('Sessione scaduta o non valida');
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
