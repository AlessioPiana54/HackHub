import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InvitationsService {
  private readonly API_URL = '/api/invitations';

  constructor(private http: HttpClient) {}

  getReceivedInvitations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/received`);
  }

  getSentInvitations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/sent`);
  }

  sendInvitation(request: any): Observable<any> {
    return this.http.post(`${this.API_URL}`, request);
  }

  respondToInvitation(invitationId: string, response: { invitoId: string, accettato: boolean }): Observable<any> {
    return this.http.patch(`${this.API_URL}/${invitationId}`, response, {
      responseType: 'text' as 'json'
    });
  }
}
