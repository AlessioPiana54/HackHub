import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InvitoDTO, CreaInvitoRequest } from '../models/invito.model';

@Injectable({
  providedIn: 'root'
})
export class InvitationsService {
  private readonly API_URL = '/api/invitations';

  constructor(private http: HttpClient) {}

  getReceivedInvitations(): Observable<InvitoDTO[]> {
    return this.http.get<InvitoDTO[]>(`${this.API_URL}/received`);
  }

  getSentInvitations(): Observable<InvitoDTO[]> {
    return this.http.get<InvitoDTO[]>(`${this.API_URL}/sent`);
  }

  sendInvitation(request: CreaInvitoRequest): Observable<InvitoDTO> {
    return this.http.post<InvitoDTO>(`${this.API_URL}`, request);
  }

  respondToInvitation(invitationId: string, response: { invitoId: string, accettato: boolean }): Observable<string> {
    return this.http.patch<string>(`${this.API_URL}/${invitationId}`, response, {
      responseType: 'text' as 'json'
    });
  }
}
