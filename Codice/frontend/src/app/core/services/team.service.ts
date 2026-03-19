import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TeamDTO, CreaTeamRequest } from '../models/team.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private readonly API_URL = '/api/teams';

  constructor(private http: HttpClient) {}

  getMyTeams(): Observable<TeamDTO[]> {
    const token = localStorage.getItem('hackhub_token');
    return this.http.get<TeamDTO[]>(`${this.API_URL}/my-teams`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
  }

  getTeamDetails(teamId: string): Observable<TeamDTO> {
    const token = localStorage.getItem('hackhub_token');
    return this.http.get<TeamDTO>(`${this.API_URL}/${teamId}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
  }

  creaTeam(request: CreaTeamRequest): Observable<any> {
    const token = localStorage.getItem('hackhub_token');
    console.log('TeamService - Token from localStorage:', token);
    console.log('TeamService - Request data:', request);
    
    const headers: { [key: string]: string } = {
      'Content-Type': 'application/json'
    };
    
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    console.log('TeamService - Headers:', headers);
    
    return this.http.post(`${this.API_URL}`, request, {
      headers: headers
    });
  }

  iscriviTeam(teamId: string, hackathonId: string): Observable<any> {
    const token = localStorage.getItem('hackhub_token');
    return this.http.post(`${this.API_URL}/${teamId}/iscrivi?hackathonId=${hackathonId}`, {}, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
  }

  abbandonaTeam(teamId: string): Observable<any> {
    const token = localStorage.getItem('hackhub_token');
    return this.http.post(`${this.API_URL}/${teamId}/abbandona`, {}, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
  }

  transferLeadership(teamId: string, newLeaderId: string): Observable<TeamDTO> {
    const token = localStorage.getItem('hackhub_token');
    return this.http.patch<TeamDTO>(`${this.API_URL}/${teamId}/leader/${newLeaderId}`, {}, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
  }

  updateTeam(teamId: string, request: import('../models/team.model').UpdateTeamRequest): Observable<TeamDTO> {
    const token = localStorage.getItem('hackhub_token');
    
    const headers: { [key: string]: string } = {
      'Content-Type': 'application/json'
    };
    
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    return this.http.put<TeamDTO>(`${this.API_URL}/${teamId}`, request, {
      headers: headers
    });
  }
}
