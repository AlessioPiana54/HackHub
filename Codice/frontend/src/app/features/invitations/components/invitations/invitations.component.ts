import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { InvitationsService } from '../../../../core/services/invitations.service';
import { AuthService } from '../../../../core/services/auth.service';
import { UserDTO } from '../../../../core/models/user.model';
import { InvitoDTO } from '../../../../core/models/invito.model';

@Component({
  selector: 'app-invitations',
  templateUrl: './invitations.component.html',
  styleUrls: ['./invitations.component.scss']
})
export class InvitationsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  currentUser: UserDTO | null = null;
  receivedInvitations: InvitoDTO[] = [];
  sentInvitations: InvitoDTO[] = [];
  isLoading = true;
  errorMessage = '';
  activeTab = 'received';

  constructor(
    private invitationsService: InvitationsService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
      });

    this.loadInvitations();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadInvitations(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Carica inviti ricevuti
    this.invitationsService.getReceivedInvitations()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (invitations) => {
          this.receivedInvitations = invitations;
        },
        error: (error: Error) => {
          console.error('Error loading received invitations:', error);
        }
      });

    // Carica inviti inviati
    this.invitationsService.getSentInvitations()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (invitations) => {
          this.sentInvitations = invitations;
          this.isLoading = false;
        },
        error: (error: Error) => {
          console.error('Error loading sent invitations:', error);
          this.isLoading = false;
          this.errorMessage = 'Failed to load invitations. Please try again.';
        }
      });
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  acceptInvitation(invitationId: string): void {
    this.invitationsService.respondToInvitation(invitationId, { invitoId: invitationId, accettato: true })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadInvitations(); // Ricarica le liste
        },
        error: (error: Error) => {
          console.error('Error accepting invitation:', error);
          this.errorMessage = 'Failed to accept invitation. Please try again.';
        }
      });
  }

  rejectInvitation(invitationId: string): void {
    this.invitationsService.respondToInvitation(invitationId, { invitoId: invitationId, accettato: false })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadInvitations(); // Ricarica le liste
        },
        error: (error: Error) => {
          console.error('Error rejecting invitation:', error);
          this.errorMessage = 'Failed to reject invitation. Please try again.';
        }
      });
  }

  withdrawInvitation(invitationId: string): void {
    // TODO: Implementare endpoint per ritirare invito quando disponibile nel backend
    console.log('Withdraw invitation:', invitationId);
  }
}
