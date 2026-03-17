import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { UserDTO } from './core/models/user.model';
import { HackathonService } from './core/services/hackathon.service';
import { HackathonSummaryDTO } from './core/models/hackathon.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  title = 'HackHub';
  isMobileMenuOpen = false;
  currentUser: UserDTO | null = null;
  myHackathons: HackathonSummaryDTO[] = [];
  judgeHackathons: HackathonSummaryDTO[] = [];
  mentorHackathons: HackathonSummaryDTO[] = [];

  constructor(
    private authService: AuthService,
    private hackathonService: HackathonService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.loadMyHackathons();
        if (user.ruolo === 'GIUDICE') {
          this.loadJudgeHackathons();
        }
        if (user.ruolo === 'MENTORE') {
          this.loadMentorHackathons();
        }
      } else {
        this.myHackathons = [];
        this.judgeHackathons = [];
        this.mentorHackathons = [];
      }
    });
  }

  loadMyHackathons(): void {
    this.hackathonService.getMyHackathons().subscribe({
      next: (hackathons) => {
        this.myHackathons = hackathons;
      },
      error: (error) => {
        console.error('Error loading my hackathons:', error);
        this.myHackathons = [];
      }
    });
  }

  loadJudgeHackathons(): void {
    this.hackathonService.getJudgeHackathons().subscribe({
      next: (hackathons) => {
        this.judgeHackathons = hackathons;
      },
      error: (error) => {
        console.error('Error loading judge hackathons:', error);
        this.judgeHackathons = [];
      }
    });
  }

  loadMentorHackathons(): void {
    this.hackathonService.getMentorHackathons().subscribe({
      next: (hackathons) => {
        this.mentorHackathons = hackathons;
      },
      error: (error) => {
        console.error('Error loading mentor hackathons:', error);
        this.mentorHackathons = [];
      }
    });
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        // Successo backend
        this.authService.clearAuth();
        this.router.navigate(['/auth/login']);
        this.isMobileMenuOpen = false;
      },
      error: (error) => {
        // Errore backend (500, 403, etc.) - comunque pulisci i dati locali
        console.warn('Backend logout failed, clearing local auth:', error);
        this.authService.clearAuth();
        this.router.navigate(['/auth/login']);
        this.isMobileMenuOpen = false;
      },
      complete: () => {
        // Questo viene chiamato solo in caso di successo
        console.log('Logout completed successfully');
      }
    });
  }
}
