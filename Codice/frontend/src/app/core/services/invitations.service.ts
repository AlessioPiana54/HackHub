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
    const token = localStorage.getItem('token');
    return this.http.get<any[]>(`${this.API_URL}/received`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  getSentInvitations(): Observable<any[]> {
    const token = localStorage.getItem('token');
    return this.http.get<any[]>(`${this.API_URL}/sent`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  sendInvitation(request: any): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.post(`${this.API_URL}`, request, {
      headers: token ? { Authorization: token } : {}
    });
  }

  respondToInvitation(invitationId: string, response: { invitoId: string, accettato: boolean }): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.patch(`${this.API_URL}/${invitationId}`, response, {
      headers: token ? { Authorization: token } : {},
      responseType: 'text' as 'json'
    });
  }
}
