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
    return this.http.get<Sottomissione[]>(`${this.API_URL}/my-submissions`);
  }

  inviaSottomissione(request: InviaSottomissioneRequest): Observable<Sottomissione> {
    return this.http.post<Sottomissione>(`${this.API_URL}`, request);
  }

  modificaSottomissione(id: string, request: ModificaSottomissioneRequest): Observable<Sottomissione> {
    return this.http.patch<Sottomissione>(`${this.API_URL}/${id}`, request);
  }

  valutaSottomissione(id: string, request: CreaValutazioneRequest): Observable<any> {
    return this.http.patch(`${this.API_URL}/${id}/evaluation`, request);
  }

  /**
   * Recupera tutte le sottomissioni per un specifico Hackathon.
   */
  getSubmissionsByHackathon(hackathonId: string): Observable<Sottomissione[]> {
    return this.http.get<Sottomissione[]>(`${this.API_URL}/hackathon/${hackathonId}`);
  }
}
