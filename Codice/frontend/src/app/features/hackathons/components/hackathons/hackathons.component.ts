import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HackathonService } from '../../../../core/services/hackathon.service';
import { TeamService } from '../../../../core/services/team.service';
import { HackathonSummaryDTO, StatoHackathon } from '../../../../core/models/hackathon.model';

@Component({
  selector: 'app-hackathons',
  templateUrl: './hackathons.component.html',
  styleUrls: ['./hackathons.component.scss']
})
export class HackathonsComponent implements OnInit {
  activeFilter = 'all';
  hackathons: HackathonSummaryDTO[] = [];
  myHackathonIds: Set<string> = new Set();
  isLoading = true;
  errorMessage = '';

  constructor(
    private hackathonService: HackathonService,
    private teamService: TeamService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadHackathons();
  }

  loadHackathons(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.hackathonService.getHackathons().subscribe({
      next: (hackathons) => {
        this.hackathons = hackathons;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load hackathons. Please try again.';
        this.isLoading = false;
      }
    });

    // Load user's registered hackathons (silently — no hard error if user has no team)
    this.hackathonService.getMyHackathons().subscribe({
      next: (myHackathons) => {
        this.myHackathonIds = new Set(myHackathons.map(h => h.id));
      },
      error: () => {
        // If user has no token or no team, just leave the set empty
        this.myHackathonIds = new Set();
      }
    });
  }

  get filteredHackathons(): HackathonSummaryDTO[] {
    if (this.activeFilter === 'all') {
      return this.hackathons;
    }
    return this.hackathons.filter(hackathon => hackathon.stato === this.activeFilter);
  }

  setFilter(filter: string): void {
    this.activeFilter = filter;
  }

  getStatusDisplay(stato: string): string {
    switch (stato) {
      case 'IN_ISCRIZIONE':
        return 'Open for Registration';
      case 'IN_CORSO':
        return 'In Progress';
      case 'CONCLUSO':
        return 'Completed';
      default:
        return stato;
    }
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  viewDetails(hackathon: HackathonSummaryDTO): void {
    this.router.navigate(['/hackathons', hackathon.id]);
  }

  isRegistered(hackathon: HackathonSummaryDTO): boolean {
    return this.myHackathonIds.has(hackathon.id);
  }

  joinHackathon(hackathon: HackathonSummaryDTO): void {
    this.teamService.getMyTeams().subscribe({
      next: (teams) => {
        if (!teams || teams.length === 0) {
          this.errorMessage = 'Devi essere in un team per iscriverti a un hackathon.';
          return;
        }
        const teamId = teams[0].id;
        this.hackathonService.joinHackathon(hackathon.id, teamId).subscribe({
          next: (response) => {
            console.log('Team successfully registered:', response);
            alert(`Team iscritto con successo a ${hackathon.nome}!`);
          },
          error: (error) => {
            this.errorMessage = error.error?.message || 'Errore durante l\'iscrizione. Riprova.';
          }
        });
      },
      error: () => {
        this.errorMessage = 'Impossibile recuperare le informazioni del team. Riprova.';
      }
    });
  }
}
