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
    return this.http.post(`${this.API_URL}`, request);
  }

  getClassifica(hackathonId: string): Observable<any> {
    return this.http.get(`${this.API_URL}/${hackathonId}/classifica`);
  }

  terminaFaseValutazione(hackathonId: string): Observable<any> {
    return this.http.patch(`${this.API_URL}/${hackathonId}/status`, {});
  }

  proclamaVincitore(hackathonId: string, teamId: string): Observable<any> {
    return this.http.post(`${this.API_URL}/${hackathonId}/winner?teamId=${teamId}`, {});
  }

  joinHackathon(hackathonId: string, teamId: string): Observable<any> {
    return this.http.post(`${this.API_URL}/${hackathonId}/join?teamId=${teamId}`, {});
  }

  getMyHackathons(): Observable<HackathonSummaryDTO[]> {
    return this.http.get<HackathonSummaryDTO[]>(`${this.API_URL}/my`);
  }

  /**
   * Recupera gli hackathon assegnati al giudice loggato.
   */
  getJudgeHackathons(): Observable<HackathonSummaryDTO[]> {
    return this.http.get<HackathonSummaryDTO[]>(`${this.API_URL}/judge/my`);
  }

  /**
   * Recupera gli hackathon assegnati al mentore loggato.
   */
  getMentorHackathons(): Observable<HackathonSummaryDTO[]> {
    return this.http.get<HackathonSummaryDTO[]>(`${this.API_URL}/mentor/my`);
  }

  /**
   * Recupera i team partecipanti a un hackathon.
   */
  getParticipants(hackathonId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/${hackathonId}/participants`);
  }
}
