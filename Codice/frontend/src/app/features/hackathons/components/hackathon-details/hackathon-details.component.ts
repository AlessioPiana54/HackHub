import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HackathonService } from '../../../../core/services/hackathon.service';
import { TeamService } from '../../../../core/services/team.service';
import { HackathonSummaryDTO } from '../../../../core/models/hackathon.model';

@Component({
  selector: 'app-hackathon-details',
  templateUrl: './hackathon-details.component.html',
  styleUrls: ['./hackathon-details.component.css']
})
export class HackathonDetailsComponent implements OnInit {
  hackathon: HackathonSummaryDTO | null = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private hackathonService: HackathonService,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadHackathonDetails(id);
    } else {
      this.errorMessage = 'Hackathon ID not found';
      this.isLoading = false;
    }
  }

  loadHackathonByIndex(index: number): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Carichiamo tutti gli hackathons e prendiamo quello per indice
    this.hackathonService.getHackathons().subscribe({
      next: (hackathons) => {
        if (hackathons[index]) {
          // Aggiungiamo i dati mancanti per la visualizzazione
          const hackathonData = hackathons[index];
          this.hackathon = {
            ...hackathonData,
            id: `hackathon-${index}`,
            descrizione: `Hackathon ${hackathonData.nome} - ${hackathonData.regolamento.substring(0, 100)}...`,
            inizioIscrizioni: hackathonData.dataInizio, // Temporaneamente usiamo dataInizio
            scadenzaIscrizioni: hackathonData.dataFine, // Temporaneamente usiamo dataFine
          };
          this.isLoading = false;
        } else {
          this.errorMessage = 'Hackathon not found';
          this.isLoading = false;
        }
      },
      error: (error) => {
        this.errorMessage = 'Failed to load hackathon details. Please try again.';
        this.isLoading = false;
      }
    });
  }

  loadHackathonDetails(id: string): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.hackathonService.getHackathonById(id).subscribe({
      next: (hackathon) => {
        this.hackathon = hackathon;
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load hackathon details. Please try again.';
        this.isLoading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/hackathons']);
  }

  joinHackathon(): void {
    if (!this.hackathon) return;

    if (this.hackathon.stato !== 'IN_ISCRIZIONE') {
      this.errorMessage = 'This hackathon is not open for registration.';
      return;
    }

    // Fetch the user's team first, then join
    this.teamService.getMyTeams().subscribe({
      next: (teams) => {
        if (!teams || teams.length === 0) {
          this.errorMessage = 'Devi essere in un team per iscriverti a un hackathon.';
          return;
        }
        const teamId = teams[0].id;
        this.hackathonService.joinHackathon(this.hackathon!.id, teamId).subscribe({
          next: (response) => {
            console.log('Team successfully registered:', response);
            alert(`Team iscritto con successo a ${this.hackathon?.nome || 'questo hackathon'}!`);
          },
          error: (error) => {
            console.error('Registration failed:', error);
            this.errorMessage = error.error?.message || 'Errore durante l\'iscrizione. Riprova.';
          }
        });
      },
      error: () => {
        this.errorMessage = 'Impossibile recuperare le informazioni del team. Riprova.';
      }
    });
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
}
