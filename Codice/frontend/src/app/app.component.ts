import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from './core/services/auth.service';
import { UserDTO } from './core/models/user.model';
import { HackathonService } from './core/services/hackathon.service';
import { HackathonSummaryDTO } from './core/models/hackathon.model';
import { PwaService } from './core/services/pwa.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  title = 'HackHub';
  isMobileMenuOpen = false;
  isAuthRoute = false;
  currentUser: UserDTO | null = null;
  myHackathons: HackathonSummaryDTO[] = [];
  judgeHackathons: HackathonSummaryDTO[] = [];
  mentorHackathons: HackathonSummaryDTO[] = [];
  organizerHackathons: HackathonSummaryDTO[] = [];
  showInstallBanner = false;

  constructor(
    private authService: AuthService,
    private hackathonService: HackathonService,
    private router: Router,
    private pwaService: PwaService
  ) {}

  ngOnInit(): void {
    this.isAuthRoute = this.router.url.startsWith('/auth');
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe((e) => {
      this.isAuthRoute = (e as NavigationEnd).urlAfterRedirects.startsWith('/auth');
    });

    this.pwaService.showInstallBanner$.subscribe(show => {
      this.showInstallBanner = show;
    });

    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      this.myHackathons = [];
      this.judgeHackathons = [];
      this.mentorHackathons = [];
      this.organizerHackathons = [];
      if (!user) return;

      switch (user.ruolo) {
        case 'GIUDICE':
          this.loadJudgeHackathons();
          break;
        case 'MENTORE':
          this.loadMentorHackathons();
          break;
        case 'ORGANIZZATORE':
          this.loadOrganizerHackathons();
          break;
        default:
          this.loadMyHackathons();
      }
    });
  }

  loadMyHackathons(): void {
    this.hackathonService.getMyHackathons().subscribe({
      next: (hackathons) => {
        this.myHackathons = hackathons;
      },
      error: () => this.myHackathons = []
    });
  }

  loadJudgeHackathons(): void {
    this.hackathonService.getJudgeHackathons().subscribe({
      next: (hackathons) => {
        this.judgeHackathons = hackathons;
      },
      error: () => this.judgeHackathons = []
    });
  }

  loadMentorHackathons(): void {
    this.hackathonService.getMentorHackathons().subscribe({
      next: (hackathons) => {
        this.mentorHackathons = hackathons;
      },
      error: () => this.mentorHackathons = []
    });
  }

  loadOrganizerHackathons(): void {
    this.hackathonService.getOrganizerHackathons().subscribe({
      next: (hackathons) => this.organizerHackathons = hackathons,
      error: () => this.organizerHackathons = []
    });
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  async installApp(): Promise<void> {
    await this.pwaService.installApp();
  }

  closeInstallBanner(): void {
    this.pwaService.hideBanner();
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        // Successo backend
        this.authService.clearAuth();
        this.router.navigate(['/auth/login']);
        this.isMobileMenuOpen = false;
      },
      error: () => {
        // Anche se il backend non risponde, la sessione locale va chiusa
        this.authService.clearAuth();
        this.router.navigate(['/auth/login']);
        this.isMobileMenuOpen = false;
      }
    });
  }
}
