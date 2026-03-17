import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserDTO, UpdateProfileRequest } from '../../core/models/user.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  currentUser: UserDTO | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    this.profileForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(2)]],
      cognome: ['', [Validators.required, Validators.minLength(2)]],
      email: [{ value: '', disabled: true }]
    });
  }

  ngOnInit(): void {
    console.log('ProfileComponent - Initializing...');
    
    // Ascolta i cambiamenti dell'utente
    this.authService.currentUser$.subscribe(user => {
      console.log('ProfileComponent - User updated:', user);
      this.currentUser = user;
      if (user) {
        this.profileForm.patchValue({
          nome: user.nome,
          cognome: user.cognome,
          email: user.email
        });
      }
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const updateData: UpdateProfileRequest = {
      nome: this.profileForm.value.nome,
      cognome: this.profileForm.value.cognome
    };

    console.log('ProfileComponent - Updating profile:', updateData);
    this.authService.updateProfile(updateData).subscribe({
      next: (updatedUser) => {
        console.log('ProfileComponent - Profile updated successfully:', updatedUser);
        this.currentUser = updatedUser;
        this.successMessage = 'Profile updated successfully!';
        this.isLoading = false;
      },
      error: (error) => {
        console.error('ProfileComponent - Error updating profile:', error);
        this.errorMessage = error.error?.message || 'Failed to update profile. Please try again.';
        this.isLoading = false;
      }
    });
  }

  get nome() { return this.profileForm.get('nome'); }
  get cognome() { return this.profileForm.get('cognome'); }
}
