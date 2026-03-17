import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Report {
  id: string;
  idHackathon: string;
  nomeHackathon: string;
  idTeam: string;
  nomeTeam: string;
  idMentore: string;
  nomeMentore: string;
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
    const token = localStorage.getItem('token');
    return this.http.get<Report[]>(`${this.API_URL}?hackathonId=${hackathonId}`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  createReport(report: { idHackathon: string, idTeam: string, descrizione: string }): Observable<Report> {
    const token = localStorage.getItem('token');
    return this.http.post<Report>(`${this.API_URL}/crea`, report, {
      headers: token ? { Authorization: token } : {}
    });
  }
}
