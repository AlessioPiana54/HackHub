import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HackathonSummaryDTO, CreaHackathonRequest, ClassificaTeamDTO, PartecipazioneDTO } from '../models/hackathon.model';

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

  creaHackathon(request: CreaHackathonRequest): Observable<HackathonSummaryDTO> {
    return this.http.post<HackathonSummaryDTO>(`${this.API_URL}`, request);
  }

  getClassifica(hackathonId: string): Observable<ClassificaTeamDTO[]> {
    return this.http.get<ClassificaTeamDTO[]>(`${this.API_URL}/${hackathonId}/classifica`);
  }

  terminaFaseValutazione(hackathonId: string): Observable<void> {
    return this.http.patch<void>(`${this.API_URL}/${hackathonId}/status`, {});
  }

  proclamaVincitore(hackathonId: string, teamId: string): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/${hackathonId}/winner?teamId=${teamId}`, {});
  }

  joinHackathon(hackathonId: string, teamId: string): Observable<PartecipazioneDTO> {
    return this.http.post<PartecipazioneDTO>(`${this.API_URL}/${hackathonId}/join?teamId=${teamId}`, {});
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
  getParticipants(hackathonId: string): Observable<PartecipazioneDTO[]> {
    return this.http.get<PartecipazioneDTO[]>(`${this.API_URL}/${hackathonId}/participants`);
  }
}
