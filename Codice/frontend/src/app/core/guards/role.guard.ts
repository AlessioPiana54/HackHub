import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.authService.hasToken()) {
      return true;
    }
    
    this.router.navigate(['/auth/login']);
    return false;
  }

  canActivateWithRoles(allowedRoles: string[]): boolean {
    if (!this.authService.hasToken()) {
      this.router.navigate(['/auth/login']);
      return false;
    }
    
    if (this.authService.hasAnyRole(allowedRoles)) {
      return true;
    }
    
    // Se non ha i ruoli richiesti, reindirizza alla dashboard
    this.router.navigate(['/dashboard']);
    return false;
  }
}
