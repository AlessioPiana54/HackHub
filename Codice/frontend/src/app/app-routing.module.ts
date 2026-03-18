import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'profile',
    loadChildren: () => import('./features/profile/profile.module').then(m => m.ProfileModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'hackathons',
    loadChildren: () => import('./features/hackathons/hackathons.module').then(m => m.HackathonsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'teams',
    loadChildren: () => import('./features/teams/teams.module').then(m => m.TeamsModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'invitations',
    loadChildren: () => import('./features/invitations/invitations.module').then(m => m.InvitationsModule),
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: '/auth/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/auth/login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
