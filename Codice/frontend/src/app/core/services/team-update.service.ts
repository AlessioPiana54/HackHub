import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TeamUpdateService {
  private teamUpdateSubject = new Subject<void>();
  
  // Observable per notificare aggiornamenti dei team
  teamUpdate$ = this.teamUpdateSubject.asObservable();
  
  // Metodo per notificare che i team sono stati aggiornati
  notifyTeamUpdate(): void {
    this.teamUpdateSubject.next();
  }
}
