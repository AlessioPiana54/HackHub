import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HackathonService } from '../../../../core/services/hackathon.service';
import { SottomissioneService } from '../../../../core/services/sottomissione.service';
import { ReportService, Report } from '../../../../core/services/report.service';
import { ToastService } from '../../../../core/services/toast.service';
import { HackathonSummaryDTO, ClassificaTeamDTO } from '../../../../core/models/hackathon.model';
import { TeamDTO } from '../../../../core/models/team.model';
import { Sottomissione } from '../../../../core/models/sottomissione.model';

type Tab = 'participants' | 'submissions' | 'reports' | 'ranking';

/**
 * Dashboard dell'organizzatore per un hackathon che ha creato:
 * team iscritti, progetti consegnati, segnalazioni dei mentori, classifica
 * e proclamazione del vincitore.
 */
@Component({
  selector: 'app-organizer-hackathon-dashboard',
  templateUrl: './organizer-hackathon-dashboard.component.html',
  styleUrls: ['./organizer-hackathon-dashboard.component.scss']
})
export class OrganizerHackathonDashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  hackathonId: string | null = null;
  hackathon: HackathonSummaryDTO | null = null;
  activeTab: Tab = 'participants';
  participants: TeamDTO[] = [];
  submissions: Sottomissione[] = [];
  reports: Report[] = [];
  ranking: ClassificaTeamDTO[] = [];
  isLoading = true;
  isProclaiming = false;

  constructor(
    private route: ActivatedRoute,
    private hackathonService: HackathonService,
    private sottomissioneService: SottomissioneService,
    private reportService: ReportService,
    private toastService: ToastService
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

  get isRankingAvailable(): boolean {
    return this.hackathon?.stato === 'IN_PREMIAZIONE' || this.hackathon?.stato === 'CONCLUSO';
  }

  get canProclaim(): boolean {
    return this.hackathon?.stato === 'IN_PREMIAZIONE';
  }

  loadData(): void {
    if (!this.hackathonId) return;
    this.isLoading = true;
    const id = this.hackathonId;

    this.hackathonService.getHackathonById(id).subscribe({
      next: (h) => {
        this.hackathon = h;
        this.isLoading = false;
        this.hackathonService.getParticipants(id).subscribe(teams => this.participants = teams);
        this.sottomissioneService.getSubmissionsByHackathon(id).subscribe(subs => this.submissions = subs);
        this.reportService.getReportsByHackathon(id).subscribe(reps => this.reports = reps);
        if (this.isRankingAvailable) {
          this.hackathonService.getClassifica(id).subscribe(r => this.ranking = r);
        } else {
          this.ranking = [];
        }
      },
      error: () => this.isLoading = false
    });
  }

  proclaimWinner(entry: ClassificaTeamDTO): void {
    if (!this.hackathonId || !this.canProclaim) return;
    const premio = this.hackathon?.premioInDenaro ?? 0;
    if (!confirm(`Proclamare "${entry.nomeTeam}" vincitore? Il premio di ${premio} € verrà versato al leader e l'hackathon sarà concluso.`)) {
      return;
    }

    this.isProclaiming = true;
    this.hackathonService.proclamaVincitore(this.hackathonId, entry.teamId).subscribe({
      next: () => {
        this.toastService.success(`${entry.nomeTeam} è il vincitore!`);
        this.isProclaiming = false;
        this.loadData();
      },
      error: () => this.isProclaiming = false
    });
  }
}
