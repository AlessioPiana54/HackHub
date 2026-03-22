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
    return this.http.get<TeamDTO[]>(`${this.API_URL}/my-teams`);
  }

  getTeamDetails(teamId: string): Observable<TeamDTO> {
    return this.http.get<TeamDTO>(`${this.API_URL}/${teamId}`);
  }

  creaTeam(request: CreaTeamRequest): Observable<any> {
    return this.http.post(`${this.API_URL}`, request);
  }



  abbandonaTeam(teamId: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/${teamId}/members/me`);
  }

  transferLeadership(teamId: string, newLeaderId: string): Observable<TeamDTO> {
    return this.http.patch<TeamDTO>(`${this.API_URL}/${teamId}/leader/${newLeaderId}`, {});
  }

  updateTeam(teamId: string, request: import('../models/team.model').UpdateTeamRequest): Observable<TeamDTO> {
    return this.http.put<TeamDTO>(`${this.API_URL}/${teamId}`, request);
  }
}
