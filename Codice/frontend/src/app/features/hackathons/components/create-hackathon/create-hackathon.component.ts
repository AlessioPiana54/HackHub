import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HackathonService } from '../../../../core/services/hackathon.service';
import { AuthService } from '../../../../core/services/auth.service';
import { CreaHackathonRequest } from '../../../../core/models/hackathon.model';
import { UserDTO, Ruolo } from '../../../../core/models/user.model';

@Component({
  selector: 'app-create-hackathon',
  templateUrl: './create-hackathon.component.html',
  styleUrls: ['./create-hackathon.component.scss']
})
export class CreateHackathonComponent implements OnInit {
  hackathonForm: FormGroup;
  isSubmitting = false;
  errorMessage = '';

  giudici: UserDTO[] = [];
  mentori: UserDTO[] = [];

  constructor(
    private fb: FormBuilder,
    private hackathonService: HackathonService,
    private authService: AuthService,
    private router: Router
  ) {
    // Imposta date di default valide
    const now = new Date();
    const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
    const nextWeek = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
    const twoWeeks = new Date(now.getTime() + 14 * 24 * 60 * 60 * 1000);
    
    this.hackathonForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      regolamento: ['', [Validators.required]],
      inizioIscrizioni: [this.formatDateTimeLocal(tomorrow), Validators.required],
      scadenzaIscrizioni: [this.formatDateTimeLocal(nextWeek), Validators.required],
      dataInizio: [this.formatDateTimeLocal(nextWeek), Validators.required],
      dataFine: [this.formatDateTimeLocal(twoWeeks), Validators.required],
      luogo: ['', [Validators.required]],
      premioInDenaro: [0, [Validators.required, Validators.min(0)]],
      idGiudice: ['', Validators.required],
      idMentori: [[], Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  // Metodo per formattare le date in formato datetime-local
  private formatDateTimeLocal(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  // Metodo per formattare le date in formato date (YYYY-MM-DD)
  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  // Metodo per creare LocalDateTime a partire da una stringa date (YYYY-MM-DD)
  private createLocalDateTime(dateString: string): any {
    // Crea una data con mezzanotte nel timezone locale
    const [year, month, day] = dateString.split('-').map(Number);
    return new Date(year, month - 1, day, 0, 0, 0);
  }

  loadUsers(): void {
    this.authService.getUsersByRole(Ruolo.GIUDICE).subscribe({
      next: (users) => this.giudici = users,
      error: (err) => console.error('Errore caricamento giudici:', err)
    });

    this.authService.getUsersByRole(Ruolo.MENTORE).subscribe({
      next: (users) => this.mentori = users,
      error: (err) => console.error('Errore caricamento mentori:', err)
    });
  }

  onSubmit(): void {
    if (this.hackathonForm.valid) {
      this.isSubmitting = true;
      this.errorMessage = '';
      
      const formValue = this.hackathonForm.value;
      const request: CreaHackathonRequest = {
        ...formValue,
        idMentori: formValue.idMentori
      };
      
      this.hackathonService.creaHackathon(request).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigate(['/hackathons']);
        },
        error: (err) => {
          this.isSubmitting = false;
          this.errorMessage = err.error?.message || 'Error occurred while creating the hackathon. Please try again.';
        }
      });
    } else {
      Object.keys(this.hackathonForm.controls).forEach(key => {
        this.hackathonForm.get(key)?.markAsTouched();
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/hackathons']);
  }
}
