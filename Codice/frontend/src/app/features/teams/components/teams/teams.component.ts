import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TeamService } from '../../../../core/services/team.service';
import { AuthService } from '../../../../core/services/auth.service';
import { InvitationsService } from '../../../../core/services/invitations.service';
import { TeamUpdateService } from '../../../../core/services/team-update.service';
import { ToastService } from '../../../../core/services/toast.service';
import { TeamDTO } from '../../../../core/models/team.model';
import { UserDTO } from '../../../../core/models/user.model';
import { InvitoDTO } from '../../../../core/models/invito.model';

@Component({
  selector: 'app-teams',
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.scss']
})
export class TeamsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  teams: TeamDTO[] = [];
  receivedInvitations: InvitoDTO[] = [];
  isLoading = true;
  errorMessage = '';
  currentUser: UserDTO | null = null;

  constructor(
    private teamService: TeamService,
    private authService: AuthService,
    private invitationsService: InvitationsService,
    private router: Router,
    private teamUpdateService: TeamUpdateService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.teamUpdateService.teamUpdate$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.loadContent());

    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
        this.loadContent();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadContent(): void {
    this.isLoading = false;
    this.receivedInvitations = [];

    if (!this.currentUser) return;

    this.loadTeams();
    if (this.currentUser.ruolo === 'UTENTE_SENZA_TEAM') {
      this.loadInvitations();
    }
  }

  loadTeams(): void {
    this.teamService.getMyTeams()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (teams) => this.teams = teams,
        error: () => {
          this.errorMessage = 'Failed to load teams. Please try again.';
          this.isLoading = false;
        }
      });
  }

  loadInvitations(): void {
    this.invitationsService.getReceivedInvitations()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (invitations) => this.receivedInvitations = invitations,
        error: () => this.receivedInvitations = []
      });
  }

  abandonTeam(teamId: string): void {
    if (!confirm('Are you sure you want to abandon this team?')) return;

    this.teamService.abbandonaTeam(teamId).subscribe({
      next: () => {
        this.setLocalRole('UTENTE_SENZA_TEAM');
        this.teams = [];
        this.teamUpdateService.notifyTeamUpdate();
      },
      error: (error: { error?: { message?: string } }) => {
        this.errorMessage = error.error?.message || 'Failed to abandon team. Please try again.';
      }
    });
  }

  clearError(): void {
    this.errorMessage = '';
    this.loadContent();
  }

  acceptInvitation(invitationId: string): void {
    this.invitationsService.respondToInvitation(invitationId, { accettato: true })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastService.success('Invito accettato: ora fai parte del team!');
          // Il ruolo cambia sul server: lo allineo anche lato client
          this.setLocalRole('MEMBRO_TEAM');
          this.teamUpdateService.notifyTeamUpdate();
        }
      });
  }

  rejectInvitation(invitationId: string): void {
    this.invitationsService.respondToInvitation(invitationId, { accettato: false })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.receivedInvitations = this.receivedInvitations.filter(i => i.id !== invitationId);
          this.toastService.info('Invito rifiutato.');
        }
      });
  }

  navigateToCreateTeam(): void {
    this.router.navigate(['/teams/create']);
  }

  hasTeams(): boolean {
    return this.teams && this.teams.length > 0;
  }

  hasInvitations(): boolean {
    return this.receivedInvitations && this.receivedInvitations.length > 0;
  }

  isUserWithoutTeam(): boolean {
    return this.currentUser?.ruolo === 'UTENTE_SENZA_TEAM';
  }

  isUserWithTeam(): boolean {
    return this.currentUser?.ruolo === 'MEMBRO_TEAM' || this.currentUser?.ruolo === 'LEADER_TEAM';
  }

  isCurrentUserLeader(team: TeamDTO): boolean {
    return !!this.currentUser && !!team?.leaderId && team.leaderId === this.currentUser.id;
  }

  getTeamMemberCount(team: TeamDTO): number {
    return team?.membriIds?.length ?? 0;
  }

  getTeamMembers(team: TeamDTO): Array<{ id: string; nome: string; cognome: string }> {
    if (!team?.membriNomi) return [];
    return team.membriNomi.map((nomeCompleto: string, index: number) => {
      const [nome, ...cognome] = nomeCompleto.split(' ');
      return { nome, cognome: cognome.join(' '), id: team.membriIds?.[index] || '' };
    });
  }

  isTeamLeader(member: { id: string }, team: TeamDTO): boolean {
    return !!member && !!team?.leaderId && member.id === team.leaderId;
  }

  viewTeamDetails(teamId: string): void {
    this.router.navigate(['/teams', teamId]);
  }

  manageTeam(teamId: string): void {
    this.router.navigate(['/teams', teamId, 'manage']);
  }

  private setLocalRole(ruolo: string): void {
    const user = this.authService.currentUser;
    if (user) {
      this.authService.updateCurrentUserLocally({ ...user, ruolo });
    }
  }
}
