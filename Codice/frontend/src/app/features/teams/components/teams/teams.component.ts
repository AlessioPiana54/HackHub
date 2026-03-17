import { Component, OnInit, OnDestroy, ChangeDetectorRef, NgZone, ApplicationRef } from '@angular/core';
import { Router } from '@angular/router';
import { TeamService } from '../../../../core/services/team.service';
import { AuthService } from '../../../../core/services/auth.service';
import { TeamUpdateService } from '../../../../core/services/team-update.service';
import { TeamDTO } from '../../../../core/models/team.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-teams',
  templateUrl: './teams.component.html',
  styleUrls: ['./teams.component.scss']
})
export class TeamsComponent implements OnInit, OnDestroy {
  teams: TeamDTO[] = [];
  receivedInvitations: any[] = [];
  isLoading = true;
  errorMessage = '';
  currentUser: any = null;
  private teamUpdateSubscription: Subscription | null = null;

  constructor(
    private teamService: TeamService,
    private authService: AuthService,
    private router: Router,
    private teamUpdateService: TeamUpdateService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private appRef: ApplicationRef
  ) {}

  ngOnInit(): void {
    console.log('TeamsComponent - Initializing...');
    
    // Ascolta gli aggiornamenti dei team
    this.teamUpdateSubscription = this.teamUpdateService.teamUpdate$.subscribe(() => {
      console.log('TeamsComponent - Team update received, refreshing all data...');
      // Forza aggiornamento completo dell'applicazione
      this.ngZone.run(() => {
        this.loadContent();
        // Forza change detection su tutta l'applicazione
        setTimeout(() => {
          this.appRef.tick();
        }, 0);
      });
    });
    
    // Forza il caricamento immediato
    this.loadContent();
    
    // Ascolta i cambiamenti dell'utente
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      console.log('TeamsComponent - User updated:', user);
      console.log('TeamsComponent - Current user after update:', this.currentUser);
      
      // Ricarica i dati quando cambia l'utente
      if (user) {
        this.loadContent();
      }
    });
    
    // Debug: check if there's a token
    console.log('TeamsComponent - Has token:', this.authService.hasToken());
    console.log('TeamsComponent - Token:', this.authService.getToken());
  }

  ngOnDestroy(): void {
    if (this.teamUpdateSubscription) {
      this.teamUpdateSubscription.unsubscribe();
    }
  }

  loadContent(): void {
    console.log('TeamsComponent - loadContent called');
    
    // Mostra sempre l'opzione di creare team
    this.isLoading = false;
    this.receivedInvitations = [];
    
    // Tenta di caricare i dati se l'utente è disponibile
    if (this.currentUser) {
      console.log('TeamsComponent - Loading data for user:', this.currentUser.ruolo);
      
      // Carica sempre i team dell'utente
      this.loadTeams();
      
      // Carica gli inviti se l'utente non ha ancora un team
      if (this.currentUser.ruolo === 'UTENTE_SENZA_TEAM') {
        this.loadInvitations();
      }
    } else {
      console.log('TeamsComponent - No user, showing create team option');
    }
  }

  loadTeams(): void {
    console.log('TeamsComponent - Loading user teams...');
    this.teamService.getMyTeams().subscribe({
      next: (teams) => {
        console.log('TeamsComponent - User teams loaded:', teams);
        this.teams = teams;
        console.log('TeamsComponent - Teams count:', this.teams.length);
        // Forza change detection immediato dentro ngZone
        this.ngZone.run(() => {
          this.cdr.detectChanges();
        });
      },
      error: (error) => {
        console.error('TeamsComponent - Error loading teams:', error);
        this.errorMessage = 'Failed to load teams. Please try again.';
        this.isLoading = false;
        this.ngZone.run(() => {
          this.cdr.detectChanges();
        });
      }
    });
  }

  loadInvitations(): void {
    console.log('TeamsComponent - Loading received invitations...');
    // Disabilitato temporaneamente - il servizio invitations non è disponibile
    this.receivedInvitations = [];
    this.isLoading = false;
  }

  loadAllTeams(): void {
    console.log('TeamsComponent - Loading all public teams...');
    // TODO: Implementare quando disponibile l'endpoint per tutti i team pubblici
    this.teams = [];
    this.isLoading = false;
  }

  abandonTeam(teamId: string): void {
    if (confirm('Are you sure you want to abandon this team?')) {
      console.log('TeamsComponent - Abandoning team:', teamId);
      this.teamService.abbandonaTeam(teamId).subscribe({
        next: () => {
          console.log('TeamsComponent - Team abandoned successfully');
          
          // Aggiorna manualmente i dati utente nell'AuthService per aggiornare la headbar
          const currentUser = this.authService.currentUser;
          if (currentUser) {
            currentUser.ruolo = 'UTENTE_SENZA_TEAM';
            // Forza aggiornamento del BehaviorSubject
            this.authService.updateCurrentUserLocally(currentUser);
          }
          
          // Ricarica i dati dei team
          setTimeout(() => {
            this.loadTeams();
            // Forza change detection per aggiornare la GUI
            this.ngZone.run(() => {
              this.appRef.tick();
            });
          }, 200);
        },
        error: (error) => {
          console.error('TeamsComponent - Error abandoning team:', error);
          this.errorMessage = error.error?.message || 'Failed to abandon team. Please try again.';
        }
      });
    }
  }

  clearError(): void {
    this.errorMessage = '';
    // Ricarichiamo i contenuti per ripristinare la view corretta
    this.loadContent();
  }

  // Metodi per inviti - disabilitati temporaneamente
  // TODO: Reimplementare quando il servizio invitations sarà disponibile
  
  acceptInvitation(invitationId: string): void {
    console.log('TeamsComponent - Accept invitation temporarily disabled');
    // TODO: Implementare quando il servizio invitations sarà disponibile
  }
  
  rejectInvitation(invitationId: string): void {
    console.log('TeamsComponent - Reject invitation temporarily disabled');
    // TODO: Implementare quando il servizio invitations sarà disponibile
  }

  navigateToCreateTeam(): void {
    console.log('TeamsComponent - Navigate to create team');
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

  // Helper methods for team display
  isCurrentUserLeader(team: any): boolean {
    if (!this.currentUser || !team) return false;
    
    // Check if user is the leader based on different possible structures
    if (team.leaderSquadra && team.leaderSquadra.id) {
      return team.leaderSquadra.id === this.currentUser.id;
    }
    
    if (team.leaderId) {
      return team.leaderId === this.currentUser.id;
    }
    
    return false;
  }

  getTeamMemberCount(team: any): number {
    if (!team) return 0;
    
    if (team.membri && Array.isArray(team.membri)) {
      return team.membri.length;
    }
    
    if (team.membriIds && Array.isArray(team.membriIds)) {
      return team.membriIds.length;
    }
    
    return 0;
  }

  getTeamMembers(team: any): any[] {
    if (!team) return [];
    
    if (team.membri && Array.isArray(team.membri)) {
      return team.membri;
    }
    
    // If we only have member names/IDs, create placeholder objects
    if (team.membriNomi && Array.isArray(team.membriNomi)) {
      return team.membriNomi.map((nome: string, index: number) => ({
        nome: nome.split(' ')[0] || nome,
        cognome: nome.split(' ')[1] || '',
        id: team.membriIds?.[index] || ''
      }));
    }
    
    return [];
  }

  isTeamLeader(member: any, team: any): boolean {
    if (!member || !team) return false;
    
    if (team.leaderSquadra && team.leaderSquadra.id) {
      return member.id === team.leaderSquadra.id;
    }
    
    if (team.leaderId) {
      return member.id === team.leaderId;
    }
    
    return false;
  }

  viewTeamDetails(teamId: string): void {
    console.log('TeamsComponent - Viewing team details:', teamId);
    // Navigate to team details page
    this.router.navigate(['/teams', teamId]);
  }

  manageTeam(teamId: string): void {
    console.log('TeamsComponent - Managing team:', teamId);
    // Navigate to team management page
    this.router.navigate(['/teams', teamId, 'manage']);
  }
}
