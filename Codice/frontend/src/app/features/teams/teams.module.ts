import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { TeamsRoutingModule } from './teams-routing.module';
import { TeamsComponent } from './components/teams/teams.component';
import { MyTeamsComponent } from './my-teams/my-teams.component';
import { CreateTeamComponent } from './components/create-team/create-team.component';
import { TeamDetailsComponent } from './components/team-details/team-details.component';
import { TeamManageComponent } from './components/team-manage/team-manage.component';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [
    TeamsComponent,
    MyTeamsComponent,
    CreateTeamComponent,
    TeamDetailsComponent,
    TeamManageComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule,
    TeamsRoutingModule,
    SharedModule
  ]
})
export class TeamsModule { }
