import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

/** Segnalazione restituita dal backend (SegnalazioneDTO). */
export interface Report {
  id: string;
  hackathonId: string;
  hackathonNome: string;
  teamId: string;
  teamNome: string;
  mentoreId: string;
  mentoreNome: string;
  descrizione: string;
  dataSegnalazione: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly API_URL = '/api/segnalazioni';

  constructor(private http: HttpClient) { }

  getReportsByHackathon(hackathonId: string): Observable<Report[]> {
    const params = new HttpParams().set('hackathonId', hackathonId);
    return this.http.get<Report[]>(this.API_URL, { params });
  }

  /** Riservato ai mentori dell'hackathon. */
  createReport(report: { idHackathon: string, idTeam: string, descrizione: string }): Observable<Report> {
    return this.http.post<Report>(this.API_URL, report);
  }
}
