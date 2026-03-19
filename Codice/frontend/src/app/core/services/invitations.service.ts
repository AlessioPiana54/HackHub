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
    const token = localStorage.getItem('hackhub_token');
    return this.http.get<any[]>(`${this.API_URL}/received`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
  }

  getSentInvitations(): Observable<any[]> {
    const token = localStorage.getItem('hackhub_token');
    return this.http.get<any[]>(`${this.API_URL}/sent`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
  }

  sendInvitation(request: any): Observable<any> {
    const token = localStorage.getItem('hackhub_token');
    return this.http.post(`${this.API_URL}`, request, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
  }

  respondToInvitation(invitationId: string, response: { invitoId: string, accettato: boolean }): Observable<any> {
    const token = localStorage.getItem('hackhub_token');
    return this.http.patch(`${this.API_URL}/${invitationId}`, response, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
      responseType: 'text' as 'json'
    });
  }
}
