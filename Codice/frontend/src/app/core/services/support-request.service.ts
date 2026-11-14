import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

/** Richiesta di supporto restituita dal backend (RichiestaSupportoDTO). */
export interface SupportRequest {
  id: string;
  hackathonId: string;
  hackathonNome: string;
  teamId: string;
  teamNome: string;
  descrizione: string;
  dataRichiesta: string;
  linkCall?: string | null;
  dataCall?: string | null;
}

export interface CreaRichiestaSupportoRequest {
  hackathonId: string;
  teamId: string;
  descrizione: string;
}

@Injectable({
  providedIn: 'root'
})
export class SupportRequestService {
  private readonly API_URL = '/api/support-requests';

  constructor(private http: HttpClient) { }

  /** Il team apre una richiesta di supporto (hackathon IN_CORSO). */
  create(request: CreaRichiestaSupportoRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(this.API_URL, request);
  }

  /** Richieste di un hackathon: riservato ai mentori dell'hackathon. */
  getRequestsByHackathon(hackathonId: string): Observable<SupportRequest[]> {
    const params = new HttpParams().set('hackathonId', hackathonId);
    return this.http.get<SupportRequest[]>(this.API_URL, { params });
  }

  /** Call proposte dai mentori per il team indicato. */
  getProposedCalls(hackathonId: string, teamId: string): Observable<SupportRequest[]> {
    const params = new HttpParams().set('hackathonId', hackathonId).set('teamId', teamId);
    return this.http.get<SupportRequest[]>(`${this.API_URL}/proposte-call`, { params });
  }

  /** Il mentore propone (o aggiorna) la call: link Google Meet o Webex e data futura. */
  proposeCall(requestId: string, linkCall: string, dataCall: string): Observable<SupportRequest> {
    return this.http.patch<SupportRequest>(`${this.API_URL}/${requestId}/call`, { linkCall, dataCall });
  }
}
