import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HackathonService } from '../../../../core/services/hackathon.service';
import { TeamService } from '../../../../core/services/team.service';
import { SottomissioneService } from '../../../../core/services/sottomissione.service';
import { SupportRequestService, SupportRequest } from '../../../../core/services/support-request.service';
import { ReportService, Report } from '../../../../core/services/report.service';
import { HackathonSummaryDTO } from '../../../../core/models/hackathon.model';
import { TeamDTO } from '../../../../core/models/team.model';
import { Sottomissione } from '../../../../core/models/sottomissione.model';

type Tab = 'details' | 'segnalazioni' | 'sottomissioni' | 'supporto';

/**
 * Vista del partecipante su un hackathon a cui il suo team è iscritto:
 * dettagli, segnalazioni sul team, consegna del progetto e richieste di supporto.
 */
@Component({
  selector: 'app-my-hackathon',
  templateUrl: './my-hackathon.component.html',
  styleUrls: ['./my-hackathon.component.scss']
})
export class MyHackathonComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  hackathon: HackathonSummaryDTO | null = null;
  myTeam: TeamDTO | null = null;
  activeTab: Tab = 'details';

  // Segnalazioni
  segnalazioni: Report[] = [];
  segnalazioneError = '';

  // Sottomissione (una per team)
  sottomissione: Sottomissione | null = null;
  formSottomissione = { linkProgetto: '', descrizione: '' };
  isEditingSottomissione = false;
  sottomissioneLoading = false;
  sottomissioneError = '';
  sottomissioneSuccess = '';

  // Supporto
  callProposte: SupportRequest[] = [];
  nuovaRichiesta = '';
  supportoLoading = false;
  supportoSuccess = '';
  supportoError = '';

  isLoading = true;
  errorMessage = '';

  private hackathonId = '';

  constructor(
    private route: ActivatedRoute,
    private hackathonService: HackathonService,
    private teamService: TeamService,
    private sottomissioneService: SottomissioneService,
    private supportService: SupportRequestService,
    private reportService: ReportService
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
        this.resetState();
        this.loadData();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get isInCorso(): boolean {
    return this.hackathon?.stato === 'IN_CORSO';
  }

  loadData(): void {
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

    this.teamService.getMyTeams().subscribe({
      next: (teams) => { if (teams.length > 0) this.myTeam = teams[0]; }
    });
  }

  setTab(tab: Tab): void {
    this.activeTab = tab;
    if (tab === 'segnalazioni') this.loadSegnalazioni();
    if (tab === 'sottomissioni') this.loadSottomissione();
    if (tab === 'supporto') this.loadCallProposte();
  }

  // --- Segnalazioni ---
  loadSegnalazioni(): void {
    this.reportService.getReportsByHackathon(this.hackathonId).subscribe({
      next: (s) => this.segnalazioni = s,
      error: () => this.segnalazioneError = 'Impossibile caricare le segnalazioni.'
    });
  }

  // --- Sottomissione ---
  loadSottomissione(): void {
    this.sottomissioneService.getMySubmissions().subscribe({
      next: (list) => {
        this.sottomissione = list.find(s => s.hackathonId === this.hackathonId) || null;
      },
      error: () => this.sottomissioneError = 'Impossibile caricare la sottomissione.'
    });
  }

  startEdit(): void {
    if (!this.sottomissione) return;
    this.formSottomissione = {
      linkProgetto: this.sottomissione.linkProgetto,
      descrizione: this.sottomissione.descrizione || ''
    };
    this.isEditingSottomissione = true;
    this.sottomissioneSuccess = '';
    this.sottomissioneError = '';
  }

  cancelEdit(): void {
    this.isEditingSottomissione = false;
    this.formSottomissione = { linkProgetto: '', descrizione: '' };
  }

  salvaSottomissione(): void {
    const linkProgetto = this.formSottomissione.linkProgetto.trim();
    if (!linkProgetto || !this.myTeam) return;

    this.sottomissioneLoading = true;
    this.sottomissioneError = '';
    this.sottomissioneSuccess = '';

    const descrizione = this.formSottomissione.descrizione.trim();
    const request$ = this.sottomissione && this.isEditingSottomissione
      ? this.sottomissioneService.modificaSottomissione(this.sottomissione.id, { linkProgetto, descrizione })
      : this.sottomissioneService.inviaSottomissione({
          idHackathon: this.hackathonId,
          idTeam: this.myTeam.id,
          linkProgetto,
          descrizione
        });

    request$.subscribe({
      next: (s) => {
        this.sottomissione = s;
        this.sottomissioneSuccess = this.isEditingSottomissione
          ? 'Sottomissione aggiornata.'
          : 'Sottomissione inviata con successo!';
        this.cancelEdit();
        this.sottomissioneLoading = false;
      },
      error: (err: { error?: { message?: string } }) => {
        this.sottomissioneError = err.error?.message || 'Errore nell\'invio della sottomissione.';
        this.sottomissioneLoading = false;
      }
    });
  }

  // --- Supporto ---
  loadCallProposte(): void {
    if (!this.myTeam) return;
    this.supportService.getProposedCalls(this.hackathonId, this.myTeam.id).subscribe({
      next: (calls) => this.callProposte = calls,
      error: () => this.callProposte = []
    });
  }

  inviaRichiestaSupporto(): void {
    const descrizione = this.nuovaRichiesta.trim();
    if (!descrizione || !this.myTeam) return;

    this.supportoLoading = true;
    this.supportoError = '';
    this.supportoSuccess = '';

    this.supportService.create({
      hackathonId: this.hackathonId,
      teamId: this.myTeam.id,
      descrizione
    }).subscribe({
      next: () => {
        this.nuovaRichiesta = '';
        this.supportoSuccess = 'Richiesta inviata: un mentore ti proporrà una call.';
        this.supportoLoading = false;
      },
      error: (err: { error?: { message?: string } }) => {
        this.supportoError = err.error?.message || 'Errore nell\'invio della richiesta.';
        this.supportoLoading = false;
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

  private resetState(): void {
    this.activeTab = 'details';
    this.segnalazioni = [];
    this.sottomissione = null;
    this.callProposte = [];
    this.cancelEdit();
  }
}
