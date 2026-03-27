import { Component, OnInit, OnDestroy, NgZone, ApplicationRef } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TeamService } from '../../../../core/services/team.service';
import { AuthService } from '../../../../core/services/auth.service';
import { TeamDTO } from '../../../../core/models/team.model';
import { TeamUpdateService } from '../../../../core/services/team-update.service';

@Component({
  selector: 'app-create-team',
  templateUrl: './create-team.component.html',
  styleUrls: ['./create-team.component.scss']
})
export class CreateTeamComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  teamForm: FormGroup;
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private teamService: TeamService,
    private authService: AuthService,
    private router: Router,
    private teamUpdateService: TeamUpdateService,
    private ngZone: NgZone,
    private appRef: ApplicationRef
  ) {
    this.teamForm = this.fb.group({
      nomeTeam: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]]
    });
  }

  ngOnInit(): void {
    console.log('CreateTeamComponent - Initializing...');
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  createTeam(): void {
    console.log('CreateTeamComponent - createTeam called');
    console.log('CreateTeamComponent - Form valid:', this.teamForm.valid);
    console.log('CreateTeamComponent - Form value:', this.teamForm.value);
    console.log('CreateTeamComponent - Token in localStorage:', localStorage.getItem('hackhub_token'));
    
    if (this.teamForm.invalid) {
      // Mark all fields as touched to trigger validation messages
      Object.keys(this.teamForm.controls).forEach(key => {
        this.teamForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const teamData = {
      nomeTeam: this.teamForm.value.nomeTeam
    };

    console.log('CreateTeamComponent - Creating team with data:', teamData);

    this.teamService.creaTeam(teamData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: TeamDTO) => {
          console.log('CreateTeamComponent - Team created successfully:', response);

          // Aggiorna manualmente i dati utente nell'AuthService per aggiornare la headbar
          const currentUser = this.authService.currentUser;
          if (currentUser) {
            currentUser.ruolo = 'LEADER_TEAM';
            // Forza aggiornamento del BehaviorSubject
            this.authService.updateCurrentUserLocally(currentUser);
          }

          // Navigate to teams page
          this.router.navigate(['/teams']).then(() => {
            // Forza ricarica dei dati dei team dopo un breve delay
            setTimeout(() => {
              // Forza change detection su tutta l'applicazione
              this.ngZone.run(() => {
                this.appRef.tick();
              });
            }, 200);
          });
        },
        error: (error: { status?: number; message?: string; error?: { message?: string } }) => {
          console.error('CreateTeamComponent - Error creating team:', error);
          console.error('CreateTeamComponent - Error status:', error.status);
          console.error('CreateTeamComponent - Error message:', error.message);
          console.error('CreateTeamComponent - Error details:', error.error);

          if (error.status === 403) {
            this.errorMessage = 'Only users without a team can create a new team.';
          } else if (error.status === 409) {
            this.errorMessage = 'A team with this name already exists. Please choose a different name.';
          } else {
            this.errorMessage = error.error?.message || 'Failed to create team. Please try again.';
          }
          this.isSubmitting = false;
        }
      });
  }

  goBack(): void {
    console.log('CreateTeamComponent - Going back to teams');
    this.router.navigate(['/teams']);
  }

  clearError(): void {
    this.errorMessage = '';
  }

  // Helper method for form validation
  get nomeTeam() { return this.teamForm.get('nomeTeam'); }
}
