import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpCtrl: HttpTestingController;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authService = jasmine.createSpyObj('AuthService', ['clearAuth']);
    router = jasmine.createSpyObj('Router', ['navigate']);
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpCtrl = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpCtrl.verify();
    localStorage.clear();
  });

  it('deve aggiungere header Authorization se il token è presente', () => {
    localStorage.setItem('hackhub_token', 'my-token');

    http.get('/api/hackathons').subscribe();
    const req = httpCtrl.expectOne('/api/hackathons');

    expect(req.request.headers.get('Authorization')).toBe('Bearer my-token');
    req.flush([]);
  });

  it('non deve aggiungere header Authorization senza token', () => {
    http.get('/api/hackathons').subscribe();
    const req = httpCtrl.expectOne('/api/hackathons');

    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush([]);
  });

  it('deve chiamare clearAuth e reindirizzare al login su risposta 401', () => {
    localStorage.setItem('hackhub_token', 'expired-token');

    http.get('/api/hackathons').subscribe({ error: () => {} });
    const req = httpCtrl.expectOne('/api/hackathons');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(authService.clearAuth).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
  });

  it('non deve interferire con errori non 401', () => {
    http.get('/api/hackathons').subscribe({ error: () => {} });
    const req = httpCtrl.expectOne('/api/hackathons');
    req.flush('Not Found', { status: 404, statusText: 'Not Found' });

    expect(authService.clearAuth).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
