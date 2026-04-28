import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HackathonService } from '../../../../core/services/hackathon.service';
import { TeamService } from '../../../../core/services/team.service';
import { HackathonSummaryDTO } from '../../../../core/models/hackathon.model';
import { TeamDTO } from '../../../../core/models/team.model';
import { Sottomissione } from '../../../../core/models/sottomissione.model';
import { Report } from '../../../../core/services/report.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-my-hackathon',
  templateUrl: './my-hackathon.component.html',
  styleUrls: ['./my-hackathon.component.scss']
})
export class MyHackathonComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  hackathon: HackathonSummaryDTO | null = null;
  myTeam: TeamDTO | null = null;
  activeTab: 'details' | 'segnalazioni' | 'sottomissioni' = 'details';

  // Segnalazioni
  segnalazioni: Report[] = [];
  segnalazioneError = '';

  // Sottomissioni
  sottomissioni: Sottomissione[] = [];
  nuovaSottomissione = { linkProgetto: '', descrizione: '' };
  sottomissioneLoading = false;
  sottomissioneError = '';
  sottomissioneSuccess = '';

  isLoading = true;
  errorMessage = '';

  private hackathonId = '';

  constructor(
    private route: ActivatedRoute,
    private hackathonService: HackathonService,
    private teamService: TeamService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.hackathonId = params.get('id') || '';
        if (!this.hackathonId) {
          this.errorMessage = 'Hackathon non trovato.';
          this.isLoading = false;
          return;
        }
        this.loadData();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadData(): void {
    // Load hackathon details
    this.hackathonService.getHackathonById(this.hackathonId).subscribe({
      next: (h) => {
        this.hackathon = h;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Impossibile caricare i dettagli dell\'hackathon.';
        this.isLoading = false;
      }
    });

    // Load user's team
    this.teamService.getMyTeams().subscribe({
      next: (teams) => { if (teams.length > 0) this.myTeam = teams[0]; }
    });
  }

  setTab(tab: 'details' | 'segnalazioni' | 'sottomissioni'): void {
    this.activeTab = tab;
    if (tab === 'segnalazioni' && this.segnalazioni.length === 0) this.loadSegnalazioni();
    if (tab === 'sottomissioni' && this.sottomissioni.length === 0) this.loadSottomissioni();
  }

  // --- Segnalazioni ---
  loadSegnalazioni(): void {
    this.http.get<Report[]>(`/api/segnalazioni?hackathonId=${this.hackathonId}`).subscribe({
      next: (s) => this.segnalazioni = s,
      error: () => this.segnalazioneError = 'Impossibile caricare le segnalazioni.'
    });
  }



  // --- Sottomissioni ---
  loadSottomissioni(): void {
    this.http.get<Sottomissione[]>('/api/submissions/my-submissions').subscribe({
      next: (s) => {
        // Filter only submissions for this hackathon
        this.sottomissioni = s.filter(sub =>
          sub.partecipazione?.hackathon?.id === this.hackathonId
        );
      },
      error: () => this.sottomissioneError = 'Impossibile caricare le sottomissioni.'
    });
  }

  inviaSottomissione(): void {
    if (!this.nuovaSottomissione.linkProgetto.trim() || !this.myTeam) return;
    this.sottomissioneLoading = true;
    this.sottomissioneError = '';
    this.sottomissioneSuccess = '';
    const body = {
      idHackathon: this.hackathonId,
      idTeam: this.myTeam.id,
      linkProgetto: this.nuovaSottomissione.linkProgetto.trim(),
      descrizione: this.nuovaSottomissione.descrizione.trim()
    };
    this.http.post<Sottomissione>('/api/submissions', body).subscribe({
      next: (s) => {
        this.sottomissioni.unshift(s);
        this.nuovaSottomissione = { linkProgetto: '', descrizione: '' };
        this.sottomissioneSuccess = 'Sottomissione inviata con successo!';
        this.sottomissioneLoading = false;
      },
      error: (err: { error?: { message?: string } }) => {
        this.sottomissioneError = err.error?.message || 'Errore nell\'invio della sottomissione.';
        this.sottomissioneLoading = false;
      }
    });
  }

  getStatusDisplay(stato: string): string {
    const map: Record<string, string> = {
      'IN_ATTESA': 'In Attesa',
      'IN_ISCRIZIONE': 'Iscrizioni Aperte',
      'IN_CORSO': 'In Corso',
      'IN_VALUTAZIONE': 'In Valutazione',
      'IN_PREMIAZIONE': 'In Premiazione',
      'CONCLUSO': 'Concluso'
    };
    return map[stato] || stato;
  }
}
