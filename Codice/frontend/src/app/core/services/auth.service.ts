import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { LoginRequest, LoginResponse, RegisterRequest, UserDTO } from '../models/user.model';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = '/api/auth';
  private readonly USER_API_URL = '/api';
  private currentUserSubject = new BehaviorSubject<UserDTO | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    const token = localStorage.getItem('hackhub_token');
    if (token) {
      const payload = this.decodeToken(token);
      const userFromToken = this.buildUserFromTokenPayload(payload);
      if (userFromToken) {
        this.currentUserSubject.next(userFromToken);
      } else {
        // Fallback: Carica l'utente se non abbiamo abbastanza dati nel token
        this.getCurrentUser().subscribe({
          error: (err) => {
            if (err.status === 401 || err.status === 403) {
              this.clearAuth();
            }
          }
        });
      }
    }
  }

  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }

  private buildUserFromTokenPayload(payload: any): UserDTO | null {
    if (!payload) return null;
    const id = payload.sub;
    const ruolo = payload.ruolo;
    const nome = payload.nome;
    const cognome = payload.cognome;
    const email = payload.email;

    if (!id || !ruolo || !nome || !cognome || !email) return null;
    return { id, ruolo, nome, cognome, email } as UserDTO;
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('hackhub_token', response.token);

        if (response.user) {
          this.currentUserSubject.next(response.user);
          return;
        }

        const payload = this.decodeToken(response.token);
        const userFromToken = this.buildUserFromTokenPayload(payload);
        if (userFromToken) {
          this.currentUserSubject.next(userFromToken);
        }
      })
    );
  }

  register(userData: RegisterRequest): Observable<any> {
    return this.http.post(`${this.API_URL}/register`, userData);
  }

  logout(): Observable<any> {
    return this.http.post(`${this.API_URL}/logout`, {}).pipe(
      finalize(() => {
        this.clearAuth();
        this.router.navigate(['/auth/login']);
      })
    );
  }

  // Metodo per logout completo (client-side)
  clearAuth(): void {
    localStorage.removeItem('hackhub_token');
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.USER_API_URL}/users/me`).pipe(
      tap(user => this.currentUserSubject.next(user))
    );
  }

  updateProfile(request: any): Observable<UserDTO> {
    return this.http.put<UserDTO>(`${this.USER_API_URL}/users/me`, request);
  }

  get currentUser(): UserDTO | null {
    return this.currentUserSubject.value;
  }

  // Metodo per impostare l'utente corrente (usato dopo registrazione)
  setCurrentUser(user: UserDTO): void {
    this.currentUserSubject.next(user);
  }

  get isAuthenticated(): boolean {
    return !!this.currentUser && !!localStorage.getItem('hackhub_token');
  }

  hasToken(): boolean {
    return !!localStorage.getItem('hackhub_token');
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
    return localStorage.getItem('hackhub_token');
  }

  // Metodo per aggiornare manualmente i dati utente (senza chiamare API)
  updateCurrentUserLocally(user: UserDTO): void {
    this.currentUserSubject.next(user);
  }

  getUsersByRole(ruolo: string): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.USER_API_URL}/users/by-role/${ruolo}`);
  }
}
