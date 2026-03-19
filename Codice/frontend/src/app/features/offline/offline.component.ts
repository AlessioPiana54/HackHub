import { Component } from '@angular/core';

@Component({
  selector: 'app-offline',
  template: `
    <div class="offline-container">
      <div class="offline-card">
        <div class="offline-icon">📡</div>
        <h1>Sei offline</h1>
        <p>Controlla la tua connessione internet e riprova.</p>
        <p class="subtitle">Alcune funzionalità potrebbero non essere disponibili.</p>
        <button class="btn btn-primary" (click)="retry()">Riprova</button>
      </div>
    </div>
  `,
  styles: [`
    .offline-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--bg-page);
      padding: var(--spacing-lg);
    }
    .offline-card {
      text-align: center;
      background: var(--bg-card);
      padding: var(--spacing-xxl);
      border-radius: var(--radius-lg);
      box-shadow: var(--shadow);
      max-width: 400px;
    }
    .offline-icon {
      font-size: 64px;
      margin-bottom: var(--spacing-lg);
    }
    .subtitle {
      color: var(--text-secondary);
      font-size: var(--font-size-sm);
    }
  `]
})
export class OfflineComponent {
  retry(): void {
    window.location.reload();
  }
}
