import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HackathonService } from '../../../../core/services/hackathon.service';
import { SottomissioneService } from '../../../../core/services/sottomissione.service';
import { HackathonSummaryDTO } from '../../../../core/models/hackathon.model';

@Component({
  selector: 'app-judge-hackathon-dashboard',
  templateUrl: './judge-hackathon-dashboard.component.html',
  styleUrls: ['./judge-hackathon-dashboard.component.scss']
})
export class JudgeHackathonDashboardComponent implements OnInit {
  hackathonId: string | null = null;
  hackathon: HackathonSummaryDTO | null = null;
  activeTab: 'submissions' | 'ranking' = 'submissions';
  submissions: any[] = [];
  ranking: any[] = [];
  isLoading = true;
  errorMessage = '';
  isClosing = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private hackathonService: HackathonService,
    private sottomissioneService: SottomissioneService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.hackathonId = params.get('id');
      if (this.hackathonId) {
        this.loadData();
      }
    });
  }

  loadData(): void {
    if (!this.hackathonId) return;
    this.isLoading = true;
    
    // In a real app we would use forkJoin, but for simplicity:
    this.hackathonService.getHackathonById(this.hackathonId).subscribe({
      next: (data) => {
        this.hackathon = data;
        this.loadSubmissions();
        this.loadRanking();
      },
      error: (err) => {
        this.errorMessage = 'Errore nel caricamento dell\'hackathon.';
        this.isLoading = false;
      }
    });
  }

  loadSubmissions(): void {
    if (!this.hackathonId) return;
    this.sottomissioneService.getSubmissionsByHackathon(this.hackathonId).subscribe({
      next: (data) => {
        this.submissions = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading submissions', err);
        this.isLoading = false;
      }
    });
  }

  loadRanking(): void {
    if (!this.hackathonId) return;
    this.hackathonService.getClassifica(this.hackathonId).subscribe({
      next: (data: any[]) => this.ranking = data,
      error: (err: any) => console.error('Error loading ranking', err)
    });
  }

  switchTab(tab: 'submissions' | 'ranking'): void {
    this.activeTab = tab;
  }

  openEvaluationModal(submission: any): void {
    const votoStr = window.prompt(`Valuta il progetto di ${submission.team.nomeTeam}\nInserisci un voto da 0 a 10:`, submission.valutazione?.voto || '');
    if (votoStr === null) return;

    const voto = parseInt(votoStr, 10);
    if (isNaN(voto) || voto < 0 || voto > 10) {
      alert('Voto non valido. Inserisci un numero tra 0 e 10.');
      return;
    }

    const giudizio = window.prompt('Inserisci un breve giudizio:', submission.valutazione?.giudizio || '');
    if (giudizio === null) return;

    const request = {
      idSottomissione: submission.id,
      voto: voto,
      giudizio: giudizio
    };

    this.sottomissioneService.valutaSottomissione(submission.id, request).subscribe({
      next: () => {
        alert('Valutazione salvata con successo!');
        this.loadSubmissions();
        this.loadRanking();
      },
      error: (err) => alert('Errore durante il salvataggio: ' + (err.error?.message || err.message))
    });
  }

  closeEvaluation(): void {
    if (!this.hackathonId || !confirm('Sei sicuro di voler chiudere la fase di valutazione? Non potrai più modificare i voti.')) return;
    
    this.isClosing = true;
    this.hackathonService.terminaFaseValutazione(this.hackathonId).subscribe({
      next: () => {
        this.loadData();
        this.isClosing = false;
      },
      error: (err) => {
        alert('Errore nella chiusura della valutazione: ' + err.error);
        this.isClosing = false;
      }
    });
  }
}
