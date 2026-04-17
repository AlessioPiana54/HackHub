import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { errorInterceptor } from './error.interceptor';
import { ToastService } from '../services/toast.service';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpCtrl: HttpTestingController;
  let toastService: jasmine.SpyObj<ToastService>;

  beforeEach(() => {
    toastService = jasmine.createSpyObj('ToastService', ['error', 'success', 'info', 'warning']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: ToastService, useValue: toastService }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpCtrl = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpCtrl.verify());

  it('deve mostrare toast di permessi negati su 403', () => {
    http.get('/api/test').subscribe({ error: () => {} });
    httpCtrl.expectOne('/api/test').flush('Forbidden', { status: 403, statusText: 'Forbidden' });
    expect(toastService.error).toHaveBeenCalledWith('Non hai i permessi per eseguire questa azione');
  });

  it('deve mostrare toast di sessione scaduta su 401', () => {
    http.get('/api/test').subscribe({ error: () => {} });
    httpCtrl.expectOne('/api/test').flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    expect(toastService.error).toHaveBeenCalledWith('Sessione scaduta o non valida');
  });

  it('deve mostrare toast di connessione su errore status 0', () => {
    http.get('/api/test').subscribe({ error: () => {} });
    httpCtrl.expectOne('/api/test').flush('', { status: 0, statusText: 'Unknown Error' });
    expect(toastService.error).toHaveBeenCalledWith('Impossibile connettersi al server');
  });

  it('deve mostrare il messaggio dell\'errore dal body su altri errori', () => {
    http.get('/api/test').subscribe({ error: () => {} });
    httpCtrl.expectOne('/api/test').flush(
      { message: 'Hackathon non trovato' },
      { status: 404, statusText: 'Not Found' }
    );
    expect(toastService.error).toHaveBeenCalledWith('Hackathon non trovato');
  });

  it('deve mostrare messaggio generico se il body non ha message', () => {
    http.get('/api/test').subscribe({ error: () => {} });
    httpCtrl.expectOne('/api/test').flush({}, { status: 500, statusText: 'Server Error' });
    expect(toastService.error).toHaveBeenCalledWith('Si è verificato un errore inaspettato');
  });

  it('non deve mostrare toast su risposta di successo', () => {
    http.get('/api/test').subscribe();
    httpCtrl.expectOne('/api/test').flush({ data: 'ok' });
    expect(toastService.error).not.toHaveBeenCalled();
  });
});
