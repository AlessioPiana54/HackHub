import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { LoginRequest, LoginResponse, RegisterRequest, UserDTO } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = '/api/auth';
  private readonly USER_API_URL = '/api';
  private currentUserSubject = new BehaviorSubject<UserDTO | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    if (token) {
      // Carica l'utente se abbiamo un token per mantenere la sessione al reload (F5)
      this.getCurrentUser().subscribe({
        error: (err) => {
          // Se il token è scaduto o non valido, puliamo la sessione
          if (err.status === 401 || err.status === 403) {
            this.clearAuth();
          }
        }
      });
    }
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        // Se la risposta include i dati utente, aggiorna il subject
        if (response.user) {
          this.currentUserSubject.next(response.user);
        }
      })
    );
  }

  register(userData: RegisterRequest): Observable<any> {
    return this.http.post(`${this.API_URL}/register`, userData);
  }

  logout(): Observable<any> {
    const token = localStorage.getItem('token');
    
    // Non pulire i dati qui, lascia che sia il componente a gestirlo
    return this.http.post(`${this.API_URL}/logout`, {}, {
      headers: token ? { Authorization: token } : {}
    });
  }

  // Metodo per logout completo (client-side)
  clearAuth(): void {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): Observable<UserDTO> {
    const token = localStorage.getItem('token');
    return this.http.get<UserDTO>(`${this.USER_API_URL}/users/me`, {
      headers: token ? { Authorization: token } : {}
    }).pipe(
      tap(user => this.currentUserSubject.next(user))
    );
  }

  updateProfile(request: any): Observable<UserDTO> {
    const token = localStorage.getItem('token');
    return this.http.put<UserDTO>(`${this.USER_API_URL}/users/me`, request, {
      headers: token ? { Authorization: token } : {}
    });
  }

  get currentUser(): UserDTO | null {
    return this.currentUserSubject.value;
  }

  // Metodo per impostare l'utente corrente (usato dopo registrazione)
  setCurrentUser(user: UserDTO): void {
    this.currentUserSubject.next(user);
  }

  get isAuthenticated(): boolean {
    return !!this.currentUser && !!localStorage.getItem('token');
  }

  hasToken(): boolean {
    return !!localStorage.getItem('token');
  }

  hasAnyRole(roles: string[]): boolean {
    const currentUser = this.currentUser;
    if (!currentUser) return false;
    
    return roles.includes(currentUser.ruolo);
  }

  getUserRole(): string {
    const currentUser = this.currentUser;
    if (!currentUser) return '';
    
    return currentUser.ruolo;
  }

  hasRole(role: string): boolean {
    return this.getUserRole() === role;
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Metodo per aggiornare manualmente i dati utente (senza chiamare API)
  updateCurrentUserLocally(user: UserDTO): void {
    this.currentUserSubject.next(user);
  }

  getUsersByRole(ruolo: string): Observable<UserDTO[]> {
    const token = localStorage.getItem('token');
    return this.http.get<UserDTO[]>(`${this.USER_API_URL}/users/by-role/${ruolo}`, {
      headers: token ? { Authorization: token } : {}
    });
  }
}
