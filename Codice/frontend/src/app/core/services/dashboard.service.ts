import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { HackathonSummaryDTO } from '../models/hackathon.model';
import { TeamDTO } from '../models/team.model';
import { InvitoDTO } from '../models/invito.model';

export interface DashboardStats {
  totalHackathons: number;
  activeTeams: number;
  pendingInvitations: number;
}

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

  getHackathons(): Observable<HackathonSummaryDTO[]> {
    return this.http.get<HackathonSummaryDTO[]>(`${this.API_URL}/hackathons`);
  }

  getMyTeams(): Observable<TeamDTO[]> {
    // Nota: l'Authorization header è aggiunto automaticamente dall'AuthInterceptor
    return this.http.get<TeamDTO[]>(`${this.API_URL}/teams/my-teams`);
  }

  getReceivedInvitations(): Observable<InvitoDTO[]> {
    // Nota: l'Authorization header è aggiunto automaticamente dall'AuthInterceptor
    return this.http.get<InvitoDTO[]>(`${this.API_URL}/invitations/received`);
  }

  /**
   * Recupera le statistiche della dashboard eseguendo chiamate parallele.
   * Utilizza forkJoin per evitare il callback hell dei subscribe annidati.
   */
  getDashboardStats(): Observable<DashboardStats> {
    return forkJoin({
      hackathons: this.getHackathons().pipe(catchError(() => of([] as HackathonSummaryDTO[]))),
      teams: this.getMyTeams().pipe(catchError(() => of([] as TeamDTO[]))),
      invitations: this.getReceivedInvitations().pipe(catchError(() => of([] as InvitoDTO[])))
    }).pipe(
      map(({ hackathons, teams, invitations }) => ({
        totalHackathons: hackathons?.length || 0,
        activeTeams: teams?.length || 0,
        pendingInvitations: invitations?.length || 0
      }))
    );
  }
}
