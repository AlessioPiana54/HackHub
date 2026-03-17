import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TeamService } from '../../../../core/services/team.service';
import { AuthService } from '../../../../core/services/auth.service';
import { InvitationsService } from '../../../../core/services/invitations.service';
import { TeamDTO } from '../../../../core/models/team.model';

@Component({
  selector: 'app-team-manage',
  templateUrl: './team-manage.component.html',
  styleUrls: ['./team-manage.component.scss']
})
export class TeamManageComponent implements OnInit {
  team: TeamDTO | null = null;
  isLoading = true;
  errorMessage = '';
  teamId: string = '';
  isEditing = false;
  editedTeamName: string = '';

  // Variabili per gli inviti
  isInviting = false;
  inviteEmail: string = '';
  inviteMessage: string = '';
  inviteError: string = '';
  
  // Utente corrente
  currentUser: any = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private teamService: TeamService,
    private authService: AuthService,
    private invitationsService: InvitationsService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    this.teamId = this.route.snapshot.paramMap.get('teamId') || '';
    if (this.teamId) {
      this.loadTeamDetails();
    } else {
      this.errorMessage = 'Team ID not provided';
      this.isLoading = false;
    }
  }

  loadTeamDetails(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.teamService.getTeamDetails(this.teamId).subscribe({
      next: (team) => {
        this.team = team;
        this.editedTeamName = team.nome || '';
        this.isLoading = false;
        console.log('TeamManageComponent - Team loaded:', team);
      },
      error: (error) => {
        console.error('TeamManageComponent - Error loading team:', error);
        this.errorMessage = 'Failed to load team details. Please try again.';
        this.isLoading = false;
      }
    });
  }

  startEditing(): void {
    this.isEditing = true;
    this.editedTeamName = this.team?.nome || '';
  }

  cancelEditing(): void {
    this.isEditing = false;
    this.editedTeamName = this.team?.nome || '';
  }

  saveTeamName(): void {
    if (!this.editedTeamName.trim()) {
      return;
    }

    console.log('TeamManageComponent - Saving team details...');
    
    const updateRequest = {
      nomeTeam: this.editedTeamName.trim()
    };

    this.teamService.updateTeam(this.teamId, updateRequest).subscribe({
      next: (updatedTeam) => {
        console.log('TeamManageComponent - Team updated successfully:', updatedTeam);
        this.team = updatedTeam;
        this.isEditing = false;
      },
      error: (error) => {
        console.error('TeamManageComponent - Error updating team:', error);
        this.errorMessage = error.error?.message || 'Failed to update team details.';
        this.isEditing = false;
      }
    });
  }

  deleteTeam(): void {
    if (confirm('Are you sure you want to delete this team? This action cannot be undone.')) {
      console.log('TeamManageComponent - Deleting team:', this.teamId);
      this.teamService.abbandonaTeam(this.teamId).subscribe({
        next: () => {
          console.log('TeamManageComponent - Team abandoned successfully');
          const currentUser = this.authService.currentUser;
          if (currentUser) {
            currentUser.ruolo = 'UTENTE_SENZA_TEAM';
            this.authService.updateCurrentUserLocally(currentUser);
          }
          this.router.navigate(['/teams']);
        },
        error: (error) => {
          console.error('TeamManageComponent - Error abandoning team:', error);
          this.errorMessage = error.error?.message || 'Failed to delete team. Please try again.';
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/teams', this.teamId]);
  }

  transferLeadership(newLeaderId: string, newLeaderName: string): void {
    if (confirm(`Are you sure you want to make ${newLeaderName} the new Team Leader? You will become a regular member.`)) {
      console.log('TeamManageComponent - Transferring leadership to:', newLeaderId);
      this.teamService.transferLeadership(this.teamId, newLeaderId).subscribe({
        next: (updatedTeam) => {
          console.log('TeamManageComponent - Leadership transferred successfully');
          this.team = updatedTeam;
          
          // Update current user role locally since they are no longer the leader
          const currentUser = this.authService.currentUser;
          if (currentUser) {
            currentUser.ruolo = 'MEMBRO_TEAM';
            this.authService.updateCurrentUserLocally(currentUser);
          }
        },
        error: (error) => {
          console.error('TeamManageComponent - Error transferring leadership:', error);
          this.errorMessage = error.error?.message || 'Failed to transfer leadership. Please try again.';
        }
      });
    }
  }

  inviteMembers(): void {
    // Al click mostriamo il form per inserire l'email
    this.isInviting = !this.isInviting;
    this.inviteEmail = '';
    this.inviteMessage = '';
    this.inviteError = '';
  }

  cancelInvite(): void {
    this.isInviting = false;
    this.inviteEmail = '';
    this.inviteMessage = '';
    this.inviteError = '';
  }

  sendInvitation(): void {
    if (!this.inviteEmail.trim()) {
      this.inviteError = 'Please enter an email address.';
      return;
    }

    this.inviteError = '';
    this.inviteMessage = '';

    const request = {
      teamId: this.teamId,
      emailDestinatario: this.inviteEmail.trim()
    };

    console.log('TeamManageComponent - Sending invitation to:', request.emailDestinatario);

    this.invitationsService.sendInvitation(request).subscribe({
      next: (response) => {
        console.log('TeamManageComponent - Invitation sent successfully', response);
        this.inviteMessage = 'Invitation sent successfully!';
        this.inviteEmail = ''; // Pulisci la form
        
        // Chiudi il form di invito dopo qualche secondo per conferma visiva
        setTimeout(() => {
          this.isInviting = false;
          this.inviteMessage = '';
        }, 3000);
      },
      error: (error) => {
        console.error('TeamManageComponent - Error sending invitation:', error);
        this.inviteError = error.error?.message || 'Failed to send invitation. Please check the email and try again.';
      }
    });
  }
}
