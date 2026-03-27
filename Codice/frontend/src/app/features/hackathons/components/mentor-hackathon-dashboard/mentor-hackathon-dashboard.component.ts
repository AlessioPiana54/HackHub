import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HackathonService } from '../../../../core/services/hackathon.service';
import { SupportRequestService, SupportRequest } from '../../../../core/services/support-request.service';
import { ReportService, Report } from '../../../../core/services/report.service';
import { HackathonSummaryDTO, PartecipazioneDTO } from '../../../../core/models/hackathon.model';

@Component({
  selector: 'app-mentor-hackathon-dashboard',
  templateUrl: './mentor-hackathon-dashboard.component.html',
  styleUrls: ['./mentor-hackathon-dashboard.component.scss']
})
export class MentorHackathonDashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  hackathonId: string | null = null;
  hackathon: HackathonSummaryDTO | null = null;
  supportRequests: SupportRequest[] = [];
  reports: Report[] = [];
  participants: PartecipazioneDTO[] = [];
  activeTab: 'support' | 'reports' = 'support';
  isLoading = true;

  // New report form state
  showReportModal = false;
  reportForm = {
    teamId: '',
    descrizione: ''
  };

  constructor(
    private route: ActivatedRoute,
    private hackathonService: HackathonService,
    private supportService: SupportRequestService,
    private reportService: ReportService
  ) { }

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
    this.isLoading = true;
    if (!this.hackathonId) return;

    this.hackathonService.getHackathonById(this.hackathonId).subscribe({
      next: (h) => {
        this.hackathon = h;
        this.loadSupportRequests();
        this.loadReports();
        this.loadParticipants();
        this.isLoading = false;
      },
      error: (err: Error) => {
        console.error('Error loading hackathon details:', err);
        this.isLoading = false;
        alert('Errore nel caricamento dell\'hackathon.');
      }
    });
  }

  loadSupportRequests(): void {
    if (!this.hackathonId) return;
    this.supportService.getRequestsByHackathon(this.hackathonId).subscribe(reqs => {
      this.supportRequests = reqs;
    });
  }

  loadReports(): void {
    if (!this.hackathonId) return;
    this.reportService.getReportsByHackathon(this.hackathonId).subscribe(reps => {
      this.reports = reps;
    });
  }

  loadParticipants(): void {
    if (!this.hackathonId) return;
    this.hackathonService.getParticipants(this.hackathonId).subscribe(teams => {
      this.participants = teams;
    });
  }

  proposeCall(req: SupportRequest): void {
    const link = prompt('Inserisci il link della call (Google Meet/Webex):');
    const data = prompt('Inserisci data e ora della call (es: 2026-03-20T10:00:00):');
    
    if (link && data) {
      this.supportService.proposeCall(req.id, link, data).subscribe({
        next: () => {
          alert('Call proposta con successo!');
          this.loadSupportRequests();
        },
        error: (err: { error?: { message?: string }; message?: string }) =>
          alert('Errore: ' + (err.error?.message || err.message))
      });
    }
  }

  openReportModal(): void {
    this.showReportModal = true;
    this.reportForm = { teamId: '', descrizione: '' };
  }

  closeReportModal(): void {
    this.showReportModal = false;
  }

  submitReport(): void {
    if (!this.hackathonId || !this.reportForm.teamId || !this.reportForm.descrizione.trim()) {
      alert('Per favore compila tutti i campi.');
      return;
    }

    this.reportService.createReport({
      idHackathon: this.hackathonId,
      idTeam: this.reportForm.teamId,
      descrizione: this.reportForm.descrizione
    }).subscribe({
      next: () => {
        alert('Segnalazione inviata con successo!');
        this.closeReportModal();
        this.loadReports();
      },
      error: (err: { error?: { message?: string }; message?: string }) =>
        alert('Errore: ' + (err.error?.message || err.message))
    });
  }
}
