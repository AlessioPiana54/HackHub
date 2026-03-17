import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HackathonSummaryDTO, CreaHackathonRequest } from '../models/hackathon.model';

@Injectable({
  providedIn: 'root'
})
export class HackathonService {
  private readonly API_URL = '/api/hackathons';

  constructor(private http: HttpClient) {}

  getHackathons(): Observable<HackathonSummaryDTO[]> {
    return this.http.get<HackathonSummaryDTO[]>(`${this.API_URL}`);
  }

  getHackathonById(id: string): Observable<HackathonSummaryDTO> {
    return this.http.get<HackathonSummaryDTO>(`${this.API_URL}/${id}`);
  }

  creaHackathon(request: CreaHackathonRequest): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.post(`${this.API_URL}`, request, {
      headers: token ? { Authorization: token } : {}
    });
  }

  getClassifica(hackathonId: string): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.get(`${this.API_URL}/${hackathonId}/classifica`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  terminaFaseValutazione(hackathonId: string): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.patch(`${this.API_URL}/${hackathonId}/status`, {}, {
      headers: token ? { Authorization: token } : {}
    });
  }

  proclamaVincitore(hackathonId: string, teamId: string): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.post(`${this.API_URL}/${hackathonId}/winner?teamId=${teamId}`, {}, {
      headers: token ? { Authorization: token } : {}
    });
  }

  joinHackathon(hackathonId: string, teamId: string): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.post(`${this.API_URL}/${hackathonId}/join?teamId=${teamId}`, {}, {
      headers: token ? { Authorization: token } : {}
    });
  }

  getMyHackathons(): Observable<HackathonSummaryDTO[]> {
    const token = localStorage.getItem('token');
    return this.http.get<HackathonSummaryDTO[]>(`${this.API_URL}/my`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  /**
   * Recupera gli hackathon assegnati al giudice loggato.
   */
  getJudgeHackathons(): Observable<HackathonSummaryDTO[]> {
    const token = localStorage.getItem('token');
    return this.http.get<HackathonSummaryDTO[]>(`${this.API_URL}/judge/my`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  /**
   * Recupera gli hackathon assegnati al mentore loggato.
   */
  getMentorHackathons(): Observable<HackathonSummaryDTO[]> {
    const token = localStorage.getItem('token');
    return this.http.get<HackathonSummaryDTO[]>(`${this.API_URL}/mentor/my`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  /**
   * Recupera i team partecipanti a un hackathon.
   */
  getParticipants(hackathonId: string): Observable<any[]> {
    const token = localStorage.getItem('token');
    return this.http.get<any[]>(`${this.API_URL}/${hackathonId}/participants`, {
      headers: token ? { Authorization: token } : {}
    });
  }
}
