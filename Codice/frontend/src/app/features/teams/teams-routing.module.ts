import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TeamsComponent } from './components/teams/teams.component';
import { MyTeamsComponent } from './my-teams/my-teams.component';
import { CreateTeamComponent } from './components/create-team/create-team.component';
import { TeamDetailsComponent } from './components/team-details/team-details.component';
import { TeamManageComponent } from './components/team-manage/team-manage.component';

const routes: Routes = [
  {
    path: '',
    component: TeamsComponent
  },
  {
    path: 'my-teams',
    component: MyTeamsComponent
  },
  {
    path: 'create',
    component: CreateTeamComponent
  },
  {
    path: ':teamId',
    component: TeamDetailsComponent
  },
  {
    path: ':teamId/manage',
    component: TeamManageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TeamsRoutingModule { }
