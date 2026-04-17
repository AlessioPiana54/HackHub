import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { LoginResponse, UserDTO } from '../models/user.model';

const MOCK_USER: UserDTO = {
  id: '1',
  nome: 'Mario',
  cognome: 'Rossi',
  email: 'mario@test.it',
  ruolo: 'ORGANIZZATORE'
};

function buildFakeToken(payload: object): string {
  const encoded = btoa(JSON.stringify(payload));
  return `header.${encoded}.signature`;
}

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  describe('inizializzazione con token in localStorage', () => {
    it('deve caricare utente dal token valido al costruttore', () => {
      const payload = { sub: '1', ruolo: 'ORGANIZZATORE', nome: 'Mario', cognome: 'Rossi', email: 'mario@test.it' };
      localStorage.setItem('hackhub_token', buildFakeToken(payload));

      TestBed.resetTestingModule();
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule], providers: [AuthService] });
      const svc = TestBed.inject(AuthService);

      expect(svc.currentUser).toBeTruthy();
      expect(svc.currentUser?.nome).toBe('Mario');
      expect(svc.currentUser?.ruolo).toBe('ORGANIZZATORE');
    });

    it('deve richiamare getCurrentUser se il token non ha tutti i campi', () => {
      const incompletePayload = { sub: '1' };
      localStorage.setItem('hackhub_token', buildFakeToken(incompletePayload));

      TestBed.resetTestingModule();
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule], providers: [AuthService] });
      const svc = TestBed.inject(AuthService);
      const httpCtrl = TestBed.inject(HttpTestingController);

      const req = httpCtrl.expectOne('/api/users/me');
      req.flush(MOCK_USER);
      httpCtrl.verify();

      expect(svc.currentUser?.nome).toBe('Mario');
    });

    it('deve chiamare clearAuth se getCurrentUser risponde 401', () => {
      const incompletePayload = { sub: '1' };
      localStorage.setItem('hackhub_token', buildFakeToken(incompletePayload));

      TestBed.resetTestingModule();
      TestBed.configureTestingModule({ imports: [HttpClientTestingModule], providers: [AuthService] });
      const svc = TestBed.inject(AuthService);
      const httpCtrl = TestBed.inject(HttpTestingController);

      const req = httpCtrl.expectOne('/api/users/me');
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
      httpCtrl.verify();

      expect(svc.currentUser).toBeNull();
      expect(localStorage.getItem('hackhub_token')).toBeNull();
    });
  });

  describe('login', () => {
    it('deve salvare il token e impostare currentUser dalla risposta', () => {
      const response: LoginResponse = { token: buildFakeToken({ sub: '1', ruolo: 'ORGANIZZATORE', nome: 'Mario', cognome: 'Rossi', email: 'mario@test.it' }), user: MOCK_USER };

      service.login({ email: 'mario@test.it', password: 'Test1234!' }).subscribe();
      http.expectOne('/api/auth/login').flush(response);

      expect(localStorage.getItem('hackhub_token')).toBe(response.token);
      expect(service.currentUser?.nome).toBe('Mario');
    });

    it('deve ricavare currentUser dal token quando user non è nella risposta', () => {
      const token = buildFakeToken({ sub: '1', ruolo: 'ORGANIZZATORE', nome: 'Luigi', cognome: 'Verdi', email: 'luigi@test.it' });
      const response: LoginResponse = { token, user: null as any };

      service.login({ email: 'luigi@test.it', password: 'Test1234!' }).subscribe();
      http.expectOne('/api/auth/login').flush(response);

      expect(service.currentUser?.nome).toBe('Luigi');
    });
  });

  describe('clearAuth', () => {
    it('deve rimuovere il token e azzerare currentUser', () => {
      localStorage.setItem('hackhub_token', 'sometoken');
      service['currentUserSubject'].next(MOCK_USER);

      service.clearAuth();

      expect(localStorage.getItem('hackhub_token')).toBeNull();
      expect(service.currentUser).toBeNull();
    });
  });

  describe('isAuthenticated', () => {
    it('deve essere true se utente e token sono presenti', () => {
      localStorage.setItem('hackhub_token', 'tok');
      service['currentUserSubject'].next(MOCK_USER);
      expect(service.isAuthenticated).toBeTrue();
    });

    it('deve essere false senza token', () => {
      service['currentUserSubject'].next(MOCK_USER);
      expect(service.isAuthenticated).toBeFalse();
    });

    it('deve essere false senza currentUser', () => {
      localStorage.setItem('hackhub_token', 'tok');
      service['currentUserSubject'].next(null);
      expect(service.isAuthenticated).toBeFalse();
    });
  });

  describe('hasRole / hasAnyRole', () => {
    beforeEach(() => service['currentUserSubject'].next(MOCK_USER));

    it('hasRole deve restituire true per ruolo corretto', () => {
      expect(service.hasRole('ORGANIZZATORE')).toBeTrue();
    });

    it('hasRole deve restituire false per ruolo diverso', () => {
      expect(service.hasRole('GIUDICE')).toBeFalse();
    });

    it('hasAnyRole deve restituire true se il ruolo è nell\'array', () => {
      expect(service.hasAnyRole(['GIUDICE', 'ORGANIZZATORE'])).toBeTrue();
    });

    it('hasAnyRole deve restituire false se nessun ruolo corrisponde', () => {
      expect(service.hasAnyRole(['GIUDICE', 'MENTORE'])).toBeFalse();
    });

    it('hasAnyRole deve restituire false senza utente', () => {
      service['currentUserSubject'].next(null);
      expect(service.hasAnyRole(['ORGANIZZATORE'])).toBeFalse();
    });
  });

  describe('getUserRole', () => {
    it('deve restituire il ruolo dell\'utente corrente', () => {
      service['currentUserSubject'].next(MOCK_USER);
      expect(service.getUserRole()).toBe('ORGANIZZATORE');
    });

    it('deve restituire stringa vuota senza utente', () => {
      service['currentUserSubject'].next(null);
      expect(service.getUserRole()).toBe('');
    });
  });

  describe('logout', () => {
    it('deve chiamare POST /api/auth/logout', () => {
      service.logout().subscribe();
      http.expectOne('/api/auth/logout').flush(null);
    });
  });

  describe('getCurrentUser', () => {
    it('deve aggiornare currentUser con la risposta dell\'API', () => {
      service.getCurrentUser().subscribe();
      http.expectOne('/api/users/me').flush(MOCK_USER);
      expect(service.currentUser?.email).toBe('mario@test.it');
    });
  });

  describe('decodeToken', () => {
    it('deve restituire null per token malformato', () => {
      const result = (service as any).decodeToken('not.a.valid.token.at.all');
      expect(result).toBeNull();
    });

    it('deve decodificare correttamente un token valido', () => {
      const payload = { sub: '42', ruolo: 'GIUDICE' };
      const token = buildFakeToken(payload);
      const result = (service as any).decodeToken(token);
      expect(result['sub']).toBe('42');
      expect(result['ruolo']).toBe('GIUDICE');
    });
  });
});
