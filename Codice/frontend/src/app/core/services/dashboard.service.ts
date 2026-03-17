import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from './auth.service';

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
    const token = this.authService.getToken();
    return this.http.get<any[]>(`${this.API_URL}/teams/my-teams`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  getReceivedInvitations(): Observable<any[]> {
    const token = this.authService.getToken();
    return this.http.get<any[]>(`${this.API_URL}/invitations/received`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  getDashboardStats(): Observable<any> {
    // Statistiche reali basate solo su endpoint esistenti
    return new Observable(observer => {
      this.getHackathons().subscribe(hackathons => {
        this.getMyTeams().subscribe(teams => {
          this.getReceivedInvitations().subscribe(invitations => {
            const stats = {
              totalHackathons: hackathons?.length || 0,
              activeTeams: teams?.length || 0,
              pendingInvitations: invitations?.length || 0
              // Rimuovo ongoingProjects - non esiste endpoint
            };
            observer.next(stats);
            observer.complete();
          }, error => {
            observer.error(error);
          });
        }, error => {
          observer.error(error);
        });
      }, error => {
        observer.error(error);
      });
    });
  }
}
