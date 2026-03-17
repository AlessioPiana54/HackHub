import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/services/auth.service';
import { DashboardService } from '../../../../core/services/dashboard.service';
import { UserDTO } from '../../../../core/models/user.model';
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
export class DashboardComponent implements OnInit {
  currentUser: UserDTO | null = null;
  stats = {
    totalHackathons: 0,
    activeTeams: 0,
    pendingInvitations: 0
    // ongoingProjects rimosso - non esiste endpoint
  };
  recentInvitations: any[] = [];
  isLoading = true;

  constructor(
    private authService: AuthService,
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
    
    this.loadDashboardStats();
  }

  loadDashboardStats(): void {
    this.dashboardService.getDashboardStats().subscribe({
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
    this.dashboardService.getReceivedInvitations().subscribe({
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
