import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TeamService } from '../../../../core/services/team.service';
import { TeamDTO } from '../../../../core/models/team.model';

@Component({
  selector: 'app-team-details',
  templateUrl: './team-details.component.html',
  styleUrls: ['./team-details.component.scss']
})
export class TeamDetailsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  team: TeamDTO | null = null;
  isLoading = true;
  errorMessage = '';
  teamId: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    this.teamId = this.route.snapshot.paramMap.get('teamId') || '';
    if (this.teamId) {
      this.loadTeamDetails();
    } else {
      this.errorMessage = 'Team ID not provided';
      this.isLoading = false;
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadTeamDetails(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.teamService.getTeamDetails(this.teamId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (team) => {
          this.team = team;
          this.isLoading = false;
          console.log('TeamDetailsComponent - Team loaded:', team);
        },
        error: (error: Error) => {
          console.error('TeamDetailsComponent - Error loading team:', error);
          this.errorMessage = 'Failed to load team details. Please try again.';
          this.isLoading = false;
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/teams']);
  }

  editTeam(): void {
    this.router.navigate(['/teams', this.teamId, 'manage']);
  }
}
