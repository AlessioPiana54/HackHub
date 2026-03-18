import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ToastMessage, ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  template: `
    <div *ngIf="toast" class="toast-container" [ngClass]="toast.type">
      <div class="toast-content">
        <span class="message">{{ toast.message }}</span>
        <button class="close-btn" (click)="close()">&times;</button>
      </div>
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 10000;
      min-width: 250px;
      padding: 15px 20px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      animation: slideIn 0.3s ease-out;
      color: white;
      font-family: 'Inter', sans-serif;
    }

    .toast-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .success { background: #28a745; }
    .error { background: #dc3545; }
    .info { background: #17a2b8; }
    .warning { background: #ffc107; color: #212529; }

    .close-btn {
      background: none;
      border: none;
      color: inherit;
      font-size: 20px;
      cursor: pointer;
      margin-left: 15px;
      line-height: 1;
    }

    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
  `]
})
export class ToastComponent implements OnInit, OnDestroy {
  toast: ToastMessage | null = null;
  private subscription: Subscription = new Subscription();

  constructor(private toastService: ToastService) {}

  ngOnInit(): void {
    this.subscription.add(
      this.toastService.toast$.subscribe(toast => {
        this.toast = toast;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  close(): void {
    this.toast = null;
  }
}
