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
      return;
    }
    this.deferredPrompt.prompt();
    const { outcome } = await this.deferredPrompt.userChoice;
    if (outcome === 'accepted') {
      this.showInstallBannerSubject.next(false);
    }
    this.deferredPrompt = null;
  }

  hideBanner(): void {
    this.showInstallBannerSubject.next(false);
  }
}
