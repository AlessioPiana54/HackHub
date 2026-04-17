import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { roleGuard } from './role.guard';
import { AuthService } from '../services/auth.service';

function makeRoute(roles: string[]): ActivatedRouteSnapshot {
  return { data: { roles } } as unknown as ActivatedRouteSnapshot;
}

const STATE = { url: '/hackathons' } as RouterStateSnapshot;

describe('roleGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authService = jasmine.createSpyObj('AuthService', ['getUserRole'], { isAuthenticated: true });
    router = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });
  });

  it('deve restituire true se autenticato e ruolo corrisponde', () => {
    authService.getUserRole.and.returnValue('ORGANIZZATORE');
    const result = TestBed.runInInjectionContext(() => roleGuard(makeRoute(['ORGANIZZATORE', 'GIUDICE']), STATE));
    expect(result).toBeTrue();
  });

  it('deve restituire true se i ruoli richiesti sono undefined (rotta senza restrizioni)', () => {
    authService.getUserRole.and.returnValue('ORGANIZZATORE');
    const route = { data: {} } as unknown as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(() => roleGuard(route, STATE));
    expect(result).toBeTrue();
  });

  it('deve reindirizzare a /unauthorized se il ruolo non è autorizzato', () => {
    authService.getUserRole.and.returnValue('MEMBRO_TEAM');
    const result = TestBed.runInInjectionContext(() => roleGuard(makeRoute(['ORGANIZZATORE']), STATE));
    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/unauthorized']);
  });

  it('deve reindirizzare al login se non autenticato', () => {
    (Object.getOwnPropertyDescriptor(authService, 'isAuthenticated')?.get as jasmine.Spy).and.returnValue(false);
    authService.getUserRole.and.returnValue('');
    const result = TestBed.runInInjectionContext(() => roleGuard(makeRoute(['ORGANIZZATORE']), STATE));
    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/auth/login'], { queryParams: { returnUrl: '/hackathons' } });
  });
});
