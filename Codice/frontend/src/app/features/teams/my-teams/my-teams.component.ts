import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TeamService } from '../../../core/services/team.service';
import { TeamDTO } from '../../../core/models/team.model';

@Component({
  selector: 'app-my-teams',
  templateUrl: './my-teams.component.html',
  styleUrls: ['./my-teams.component.scss']
})
export class MyTeamsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  teams: TeamDTO[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(private teamService: TeamService) {}

  ngOnInit(): void {
    console.log('MyTeamsComponent - Initializing');
    this.loadMyTeams();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadMyTeams(): void {
    console.log('MyTeamsComponent - Loading teams...');
    this.isLoading = true;
    this.errorMessage = '';

    this.teamService.getMyTeams()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (teams) => {
          console.log('MyTeamsComponent - Teams loaded:', teams);
          this.teams = teams || []; // Ensure it's an array
          this.isLoading = false;
          console.log('MyTeamsComponent - Teams count:', this.teams.length);
        },
        error: (error: Error) => {
          console.error('MyTeamsComponent - Error loading teams:', error);
          this.errorMessage = 'Failed to load teams. Please try again.';
          this.isLoading = false;
        }
      });
  }

  getTeamDetails(teamId: string): void {
    // Navigate to team details (to be implemented when needed)
    console.log('Navigate to team details:', teamId);
  }

  abandonTeam(teamId: string): void {
    if (confirm('Are you sure you want to abandon this team?')) {
      console.log('MyTeamsComponent - Abandoning team:', teamId);
      this.teamService.abbandonaTeam(teamId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            console.log('MyTeamsComponent - Team abandoned successfully');
            this.loadMyTeams(); // Reload teams after abandoning
          },
          error: (error: { error?: { message?: string } }) => {
            console.error('MyTeamsComponent - Error abandoning team:', error);
            this.errorMessage = error.error?.message || 'Failed to abandon team. Please try again.';
          }
        });
    }
  }
}
