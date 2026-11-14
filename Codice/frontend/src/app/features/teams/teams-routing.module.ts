import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TeamsComponent } from './components/teams/teams.component';
import { MyTeamsComponent } from './my-teams/my-teams.component';
import { CreateTeamComponent } from './components/create-team/create-team.component';
import { TeamDetailsComponent } from './components/team-details/team-details.component';
import { TeamManageComponent } from './components/team-manage/team-manage.component';
import { roleGuard } from '../../core/guards/role.guard';

const PARTECIPANTI = ['UTENTE_SENZA_TEAM', 'MEMBRO_TEAM', 'LEADER_TEAM'];

const routes: Routes = [
  {
    path: '',
    component: TeamsComponent,
    canActivate: [roleGuard],
    data: { roles: PARTECIPANTI }
  },
  {
    path: 'my-teams',
    component: MyTeamsComponent,
    canActivate: [roleGuard],
    data: { roles: PARTECIPANTI }
  },
  {
    path: 'create',
    component: CreateTeamComponent,
    canActivate: [roleGuard],
    data: { roles: ['UTENTE_SENZA_TEAM'] }
  },
  {
    path: ':teamId',
    component: TeamDetailsComponent
  },
  {
    path: ':teamId/manage',
    component: TeamManageComponent,
    canActivate: [roleGuard],
    data: { roles: ['LEADER_TEAM'] }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TeamsRoutingModule { }
