import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../../../core/services/auth.service';
import { DashboardService, DashboardStats } from '../../../../core/services/dashboard.service';
import { UserDTO } from '../../../../core/models/user.model';
import { InvitoDTO } from '../../../../core/models/invito.model';
import { Router } from '@angular/router';

interface Activity {
  icon: string;
  title: string;
  time: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  currentUser: UserDTO | null = null;
  stats: DashboardStats = {
    totalHackathons: 0,
    activeTeams: 0,
    pendingInvitations: 0
  };
  recentInvitations: InvitoDTO[] = [];
  isLoading = true;

  constructor(
    private authService: AuthService,
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });

    this.loadDashboardStats();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadDashboardStats(): void {
    this.dashboardService.getDashboardStats()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.stats = stats;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading dashboard stats:', error);
          this.isLoading = false;
          // Fallback a zero se c'è errore
          this.stats = {
            totalHackathons: 0,
            activeTeams: 0,
            pendingInvitations: 0
          };
        }
      });

    // Carica inviti ricevuti
    this.dashboardService.getReceivedInvitations()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (invitations) => {
          this.recentInvitations = invitations;
        },
        error: (error) => {
          console.error('Error loading invitations:', error);
          this.recentInvitations = [];
        }
      });
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}
