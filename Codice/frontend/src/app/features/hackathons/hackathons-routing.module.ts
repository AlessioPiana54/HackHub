import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HackathonsComponent } from './components/hackathons/hackathons.component';
import { CreateHackathonComponent } from './components/create-hackathon/create-hackathon.component';
import { HackathonDetailsComponent } from './components/hackathon-details/hackathon-details.component';
import { MyHackathonComponent } from './components/my-hackathon/my-hackathon.component';
import { JudgeHackathonDashboardComponent } from './components/judge-hackathon-dashboard/judge-hackathon-dashboard.component';
import { MentorHackathonDashboardComponent } from './components/mentor-hackathon-dashboard/mentor-hackathon-dashboard.component';
import { OrganizerHackathonDashboardComponent } from './components/organizer-hackathon-dashboard/organizer-hackathon-dashboard.component';
import { roleGuard } from '../../core/guards/role.guard';

const PARTECIPANTI = ['UTENTE_SENZA_TEAM', 'MEMBRO_TEAM', 'LEADER_TEAM'];

const routes: Routes = [
  {
    path: 'create',
    component: CreateHackathonComponent,
    canActivate: [roleGuard],
    data: { roles: ['ORGANIZZATORE'] }
  },
  {
    path: 'organizer/:id',
    component: OrganizerHackathonDashboardComponent,
    canActivate: [roleGuard],
    data: { roles: ['ORGANIZZATORE'] }
  },
  {
    path: 'my/:id',
    component: MyHackathonComponent,
    canActivate: [roleGuard],
    data: { roles: PARTECIPANTI }
  },
  {
    path: 'judge/:id',
    component: JudgeHackathonDashboardComponent,
    canActivate: [roleGuard],
    data: { roles: ['GIUDICE'] }
  },
  {
    path: 'mentor/:id',
    component: MentorHackathonDashboardComponent,
    canActivate: [roleGuard],
    data: { roles: ['MENTORE'] }
  },
  {
    path: ':id',
    component: HackathonDetailsComponent
  },
  {
    path: '',
    component: HackathonsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HackathonsRoutingModule { }
