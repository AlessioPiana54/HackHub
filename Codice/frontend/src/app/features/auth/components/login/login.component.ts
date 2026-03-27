import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../../../core/services/auth.service';
import { LoginRequest } from '../../../../core/models/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  loginForm: FormGroup;
  isLoading = false;
  submitted = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const loginData: LoginRequest = {
      email: this.loginForm.value.email?.trim(),
      password: this.loginForm.value.password
    };

    this.authService.login(loginData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          // Non chiamare getCurrentUser() automaticamente per evitare errore 500
          // Vai direttamente alla dashboard
          this.router.navigate(['/dashboard']);
        },
        error: (error: { error?: { message?: string }; message?: string }) => {
          console.error('Login error:', error);
          this.errorMessage = error.error?.message || error.message || 'Login failed. Please try again.';
          this.isLoading = false;
        },
        complete: () => {
          this.isLoading = false;
        }
      });
  }

  get f() {
    return this.loginForm.controls;
  }

  clearAuth(): void {
    this.authService.clearAuth();
  }
}
