import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HackathonService } from '../../../../core/services/hackathon.service';
import { SottomissioneService } from '../../../../core/services/sottomissione.service';
import { HackathonSummaryDTO, ClassificaTeamDTO } from '../../../../core/models/hackathon.model';
import { Sottomissione } from '../../../../core/models/sottomissione.model';

@Component({
  selector: 'app-judge-hackathon-dashboard',
  templateUrl: './judge-hackathon-dashboard.component.html',
  styleUrls: ['./judge-hackathon-dashboard.component.scss']
})
export class JudgeHackathonDashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  hackathonId: string | null = null;
  hackathon: HackathonSummaryDTO | null = null;
  activeTab: 'submissions' | 'ranking' = 'submissions';
  submissions: Sottomissione[] = [];
  ranking: ClassificaTeamDTO[] = [];
  isLoading = true;
  errorMessage = '';
  isClosing = false;
  showEvaluationModal = false;
  selectedSubmission: Sottomissione | null = null;
  evaluationForm = {
    voto: 0,
    giudizio: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private hackathonService: HackathonService,
    private sottomissioneService: SottomissioneService
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.hackathonId = params.get('id');
        if (this.hackathonId) {
          this.loadData();
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
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
      error: () => {
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
      error: () => {
        this.isLoading = false;
      }
    });
  }

  /** La classifica esiste solo in premiazione o a hackathon concluso. */
  get isRankingAvailable(): boolean {
    return this.hackathon?.stato === 'IN_PREMIAZIONE' || this.hackathon?.stato === 'CONCLUSO';
  }

  loadRanking(): void {
    if (!this.hackathonId || !this.isRankingAvailable) {
      this.ranking = [];
      return;
    }
    this.hackathonService.getClassifica(this.hackathonId).subscribe({
      next: (data: ClassificaTeamDTO[]) => this.ranking = data
    });
  }

  isEvaluated(submission: Sottomissione): boolean {
    return submission.voto !== null && submission.voto !== undefined;
  }

  get canEvaluate(): boolean {
    return this.hackathon?.stato === 'IN_VALUTAZIONE';
  }

  switchTab(tab: 'submissions' | 'ranking'): void {
    this.activeTab = tab;
  }

  openEvaluationModal(submission: Sottomissione): void {
    // Ogni sottomissione si valuta una sola volta
    if (this.isEvaluated(submission) || !this.canEvaluate) return;
    this.selectedSubmission = submission;
    this.evaluationForm = { voto: 0, giudizio: '' };
    this.showEvaluationModal = true;
  }

  closeEvaluationModal(): void {
    this.showEvaluationModal = false;
    this.selectedSubmission = null;
  }

  submitEvaluation(): void {
    if (!this.selectedSubmission) return;

    if (this.evaluationForm.voto < 0 || this.evaluationForm.voto > 10) {
      alert('Voto non valido. Inserisci un numero tra 0 e 10.');
      return;
    }
    if (!this.evaluationForm.giudizio.trim()) {
      alert('Il giudizio è obbligatorio.');
      return;
    }

    const request = {
      voto: this.evaluationForm.voto,
      giudizio: this.evaluationForm.giudizio
    };

    this.sottomissioneService.valutaSottomissione(this.selectedSubmission.id, request).subscribe({
      next: () => {
        alert('Valutazione salvata con successo!');
        this.closeEvaluationModal();
        this.loadSubmissions();
        this.loadRanking();
      },
      error: () => { /* messaggio mostrato dal toast dell'errorInterceptor */ }
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
      error: () => {
        // Il messaggio del backend è già mostrato dal toast dell'errorInterceptor
        this.isClosing = false;
      }
    });
  }
}
