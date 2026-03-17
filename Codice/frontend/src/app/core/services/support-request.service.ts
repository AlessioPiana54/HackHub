import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SupportRequest {
  id: string;
  idHackathon: string;
  idTeam: string;
  nomeTeam: string;
  descrizione: string;
  dataRichiesta: string;
  linkCall?: string;
  dataCall?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SupportRequestService {
  private readonly API_URL = '/api/support-requests';

  constructor(private http: HttpClient) { }

  getRequestsByHackathon(hackathonId: string): Observable<SupportRequest[]> {
    const token = localStorage.getItem('token');
    return this.http.get<SupportRequest[]>(`${this.API_URL}?hackathonId=${hackathonId}`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  proposeCall(requestId: string, linkCall: string, dataCall: string): Observable<SupportRequest> {
    const token = localStorage.getItem('token');
    return this.http.patch<SupportRequest>(`${this.API_URL}/${requestId}/call`, { linkCall, dataCall }, {
      headers: token ? { Authorization: token } : {}
    });
  }
}
