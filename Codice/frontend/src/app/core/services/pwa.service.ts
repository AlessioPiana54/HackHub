import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PwaService {
  private deferredPrompt: any;
  private showInstallBannerSubject = new BehaviorSubject<boolean>(false);
  showInstallBanner$ = this.showInstallBannerSubject.asObservable();

  constructor() {
    window.addEventListener('beforeinstallprompt', (e) => {
      console.log('PwaService: beforeinstallprompt event fired');
      // Prevent Chrome 67 and earlier from automatically showing the prompt
      e.preventDefault();
      // Stash the event so it can be triggered later.
      this.deferredPrompt = e;
      this.showInstallBannerSubject.next(true);
    });

    window.addEventListener('appinstalled', () => {
      this.showInstallBannerSubject.next(false);
      this.deferredPrompt = null;
    });
  }

  async installApp(): Promise<void> {
    if (!this.deferredPrompt) {
      console.warn('PwaService: No deferredPrompt available to show');
      return;
    }
    console.log('PwaService: Triggering install prompt');
    this.deferredPrompt.prompt();
    const { outcome } = await this.deferredPrompt.userChoice;
    console.log(`PwaService: User response to install prompt: ${outcome}`);
    if (outcome === 'accepted') {
      this.showInstallBannerSubject.next(false);
    }
    this.deferredPrompt = null;
  }

  hideBanner(): void {
    this.showInstallBannerSubject.next(false);
  }
}
