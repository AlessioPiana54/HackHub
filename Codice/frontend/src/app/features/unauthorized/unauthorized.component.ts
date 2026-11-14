import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * Pagina mostrata da roleGuard quando l'utente è autenticato ma il suo ruolo
 * non consente di aprire la rotta richiesta.
 */
@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="unauthorized-container">
      <div class="unauthorized-card">
        <div class="unauthorized-icon">🔒</div>
        <h1>Accesso non consentito</h1>
        <p>Il tuo ruolo non permette di aprire questa pagina.</p>
        <a class="btn btn-primary" routerLink="/dashboard">Torna alla dashboard</a>
      </div>
    </div>
  `,
  styles: [`
    .unauthorized-container {
      min-height: 60vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: var(--spacing-lg);
    }
    .unauthorized-card {
      text-align: center;
      background: var(--bg-card);
      padding: var(--spacing-xxl);
      border-radius: var(--radius-lg);
      box-shadow: var(--shadow);
      max-width: 420px;
    }
    .unauthorized-icon {
      font-size: 64px;
      margin-bottom: var(--spacing-lg);
    }
  `]
})
export class UnauthorizedComponent {}
