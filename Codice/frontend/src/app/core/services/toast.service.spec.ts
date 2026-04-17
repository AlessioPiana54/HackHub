import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ToastService, ToastMessage } from './toast.service';

describe('ToastService', () => {
  let service: ToastService;
  let emitted: (ToastMessage | null)[];

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [ToastService] });
    service = TestBed.inject(ToastService);
    emitted = [];
    service.toast$.subscribe(v => emitted.push(v));
  });

  it('deve emettere il messaggio con tipo e durata corretti', fakeAsync(() => {
    service.show('ciao', 'success', 1000);
    expect(emitted[emitted.length - 1]).toEqual({ message: 'ciao', type: 'success', duration: 1000 });

    tick(1000);
    expect(emitted[emitted.length - 1]).toBeNull();
  }));

  it('success() deve emettere un toast di tipo success', () => {
    service.success('Operazione completata');
    const last = emitted[emitted.length - 1] as ToastMessage;
    expect(last.type).toBe('success');
    expect(last.message).toBe('Operazione completata');
  });

  it('error() deve emettere un toast di tipo error', () => {
    service.error('Errore generico');
    const last = emitted[emitted.length - 1] as ToastMessage;
    expect(last.type).toBe('error');
    expect(last.message).toBe('Errore generico');
  });

  it('info() deve emettere un toast di tipo info', () => {
    service.info('Informazione');
    const last = emitted[emitted.length - 1] as ToastMessage;
    expect(last.type).toBe('info');
  });

  it('warning() deve emettere un toast di tipo warning', () => {
    service.warning('Attenzione');
    const last = emitted[emitted.length - 1] as ToastMessage;
    expect(last.type).toBe('warning');
  });

  it('deve azzerare il toast dopo la durata', fakeAsync(() => {
    service.show('test', 'info', 500);
    expect(emitted[emitted.length - 1]).not.toBeNull();

    tick(500);
    expect(emitted[emitted.length - 1]).toBeNull();
  }));

  it('il valore iniziale deve essere null', () => {
    expect(emitted[0]).toBeNull();
  });
});
