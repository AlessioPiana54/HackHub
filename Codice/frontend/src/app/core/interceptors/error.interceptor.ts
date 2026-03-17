import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 403) {
          // Mostra toast per errore 403
          this.showForbiddenToast();
          return throwError(() => new Error('Access denied'));
        }
        
        return throwError(() => error);
      })
    );
  }

  private showForbiddenToast(): void {
    // Crea e mostra un toast per errore 403
    const toast = document.createElement('div');
    toast.className = 'error-toast';
    toast.innerHTML = `
      <style>
        .error-toast {
          position: fixed;
          top: 20px;
          right: 20px;
          background: #dc3545;
          color: white;
          padding: 12px 20px;
          border-radius: 4px;
          box-shadow: 0 2px 10px rgba(0,0,0,0.2);
          z-index: 9999;
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
          font-size: 14px;
          max-width: 300px;
        }
      </style>
      Non hai i permessi per eseguire questa azione
    `;
    
    document.body.appendChild(toast);
    
    // Rimuovi toast dopo 3 secondi
    setTimeout(() => {
      if (toast.parentNode) {
        toast.parentNode.removeChild(toast);
      }
    }, 3000);
  }
}
