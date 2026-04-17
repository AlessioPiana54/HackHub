import { Component } from '@angular/core';
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { BehaviorSubject } from 'rxjs';
import { HasRoleDirective } from './has-role.directive';
import { AuthService } from '../../core/services/auth.service';
import { UserDTO } from '../../core/models/user.model';

const ORGANIZZATORE: UserDTO = { id: '1', nome: 'Mario', cognome: 'Rossi', email: 'm@t.it', ruolo: 'ORGANIZZATORE' };
const GIUDICE: UserDTO = { id: '2', nome: 'Luigi', cognome: 'Verdi', email: 'l@t.it', ruolo: 'GIUDICE' };

@Component({
  template: `<span *appHasRole="allowedRoles" id="target">Visibile</span>`,
})
class TestHostComponent {
  allowedRoles: string[] = ['ORGANIZZATORE'];
}

describe('HasRoleDirective', () => {
  let fixture: ComponentFixture<TestHostComponent>;
  let userSubject: BehaviorSubject<UserDTO | null>;
  let authService: Partial<AuthService>;

  beforeEach(() => {
    userSubject = new BehaviorSubject<UserDTO | null>(null);
    authService = {
      currentUser$: userSubject.asObservable(),
      hasAnyRole: (roles: string[]) => {
        const user = userSubject.value;
        return !!user && roles.includes(user.ruolo);
      }
    };

    TestBed.configureTestingModule({
      declarations: [TestHostComponent, HasRoleDirective],
      providers: [{ provide: AuthService, useValue: authService }]
    });

    fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();
  });

  it('non deve mostrare l\'elemento se l\'utente non ha il ruolo', () => {
    userSubject.next(GIUDICE);
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#target'))).toBeNull();
  });

  it('deve mostrare l\'elemento se l\'utente ha il ruolo', () => {
    userSubject.next(ORGANIZZATORE);
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#target'))).not.toBeNull();
  });

  it('deve nascondere l\'elemento quando il ruolo cambia', () => {
    userSubject.next(ORGANIZZATORE);
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#target'))).not.toBeNull();

    userSubject.next(GIUDICE);
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#target'))).toBeNull();
  });

  it('non deve mostrare l\'elemento senza utente autenticato', () => {
    userSubject.next(null);
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#target'))).toBeNull();
  });

  it('deve aggiornare la vista quando cambiano i ruoli richiesti', () => {
    userSubject.next(GIUDICE);
    fixture.componentInstance.allowedRoles = ['GIUDICE'];
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#target'))).not.toBeNull();
  });
});
