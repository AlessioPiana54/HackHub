import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Sottomissione, InviaSottomissioneRequest, ModificaSottomissioneRequest, CreaValutazioneRequest } from '../models/sottomissione.model';

@Injectable({
  providedIn: 'root'
})
export class SottomissioneService {
  private readonly API_URL = '/api/submissions';

  constructor(private http: HttpClient) {}

  getMySubmissions(): Observable<Sottomissione[]> {
    const token = localStorage.getItem('token');
    return this.http.get<Sottomissione[]>(`${this.API_URL}/my-submissions`, {
      headers: token ? { Authorization: token } : {}
    });
  }

  inviaSottomissione(request: InviaSottomissioneRequest): Observable<Sottomissione> {
    const token = localStorage.getItem('token');
    return this.http.post<Sottomissione>(`${this.API_URL}`, request, {
      headers: token ? { Authorization: token } : {}
    });
  }

  modificaSottomissione(request: ModificaSottomissioneRequest): Observable<Sottomissione> {
    const token = localStorage.getItem('token');
    return this.http.put<Sottomissione>(`${this.API_URL}/modifica`, request, {
      headers: token ? { Authorization: token } : {}
    });
  }

  valutaSottomissione(id: string, request: CreaValutazioneRequest): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.patch(`${this.API_URL}/${id}/evaluation`, request, {
      headers: token ? { Authorization: token } : {}
    });
  }

  /**
   * Recupera tutte le sottomissioni per un specifico Hackathon.
   */
  getSubmissionsByHackathon(hackathonId: string): Observable<Sottomissione[]> {
    const token = localStorage.getItem('token');
    return this.http.get<Sottomissione[]>(`${this.API_URL}/hackathon/${hackathonId}`, {
      headers: token ? { Authorization: token } : {}
    });
  }
}
