import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Functional Guard per il controllo dei ruoli degli utenti.
 * Verifica se l'utente è autenticato e se il suo ruolo è tra quelli permessi
 * definiti nei dati della rotta (data: { roles: [...] }).
 */
export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles = route.data['roles'] as string[];
  const userRole = authService.getUserRole();

  // Verifica autenticazione e corrispondenza ruolo
  if (authService.isAuthenticated && (!expectedRoles || expectedRoles.includes(userRole))) {
    return true;
  }

  // Se non autenticato, reindirizza al login
  if (!authService.isAuthenticated) {
    router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  // Se autenticato ma con ruolo non autorizzato, reindirizza alla pagina unauthorized
  router.navigate(['/unauthorized']);
  return false;
};
