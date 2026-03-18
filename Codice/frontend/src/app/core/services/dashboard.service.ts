import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';

/**
 * Servizio per il recupero dei dati della Dashboard.
 */
@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly API_URL = '/api';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getHackathons(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/hackathons`);
  }

  getMyTeams(): Observable<any[]> {
    // Nota: l'Authorization header è aggiunto automaticamente dall'AuthInterceptor
    return this.http.get<any[]>(`${this.API_URL}/teams/my-teams`);
  }

  getReceivedInvitations(): Observable<any[]> {
    // Nota: l'Authorization header è aggiunto automaticamente dall'AuthInterceptor
    return this.http.get<any[]>(`${this.API_URL}/invitations/received`);
  }

  /**
   * Recupera le statistiche della dashboard eseguendo chiamate parallele.
   * Utilizza forkJoin per evitare il callback hell dei subscribe annidati.
   */
  getDashboardStats(): Observable<any> {
    return forkJoin({
      hackathons: this.getHackathons().pipe(catchError(() => of([]))),
      teams: this.getMyTeams().pipe(catchError(() => of([]))),
      invitations: this.getReceivedInvitations().pipe(catchError(() => of([])))
    }).pipe(
      map(({ hackathons, teams, invitations }) => ({
        totalHackathons: hackathons?.length || 0,
        activeTeams: teams?.length || 0,
        pendingInvitations: invitations?.length || 0
      }))
    );
  }
}
