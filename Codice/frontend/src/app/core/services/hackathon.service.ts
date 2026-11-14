import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HackathonSummaryDTO, CreaHackathonRequest, ClassificaTeamDTO } from '../models/hackathon.model';
import { TeamDTO } from '../models/team.model';

interface MessageResponse {
  message: string;
}

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

  /** Chiude la valutazione (giudice): il backend risponde con testo semplice. */
  terminaFaseValutazione(hackathonId: string): Observable<string> {
    return this.http.patch(`${this.API_URL}/${hackathonId}/status`, {}, { responseType: 'text' });
  }

  /** Proclama il vincitore (organizzatore, IN_PREMIAZIONE): il backend risponde con testo semplice. */
  proclamaVincitore(hackathonId: string, teamId: string): Observable<string> {
    return this.http.post(`${this.API_URL}/${hackathonId}/winner`, {}, {
      params: { teamId },
      responseType: 'text'
    });
  }

  joinHackathon(hackathonId: string, teamId: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.API_URL}/${hackathonId}/join`, {}, { params: { teamId } });
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
   * Recupera gli hackathon creati dall'organizzatore loggato.
   */
  getOrganizerHackathons(): Observable<HackathonSummaryDTO[]> {
    return this.http.get<HackathonSummaryDTO[]>(`${this.API_URL}/organizer/my`);
  }

  /**
   * Recupera i team partecipanti a un hackathon.
   */
  getParticipants(hackathonId: string): Observable<TeamDTO[]> {
    return this.http.get<TeamDTO[]>(`${this.API_URL}/${hackathonId}/participants`);
  }
}
