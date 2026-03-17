import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HackathonsRoutingModule } from './hackathons-routing.module';
import { HackathonsComponent } from './components/hackathons/hackathons.component';
import { CreateHackathonComponent } from './components/create-hackathon/create-hackathon.component';
import { HackathonDetailsComponent } from './components/hackathon-details/hackathon-details.component';
import { MyHackathonComponent } from './components/my-hackathon/my-hackathon.component';
import { JudgeHackathonDashboardComponent } from './components/judge-hackathon-dashboard/judge-hackathon-dashboard.component';
import { MentorHackathonDashboardComponent } from './components/mentor-hackathon-dashboard/mentor-hackathon-dashboard.component';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [
    HackathonsComponent,
    CreateHackathonComponent,
    HackathonDetailsComponent,
    MyHackathonComponent,
    JudgeHackathonDashboardComponent,
    MentorHackathonDashboardComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    HackathonsRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [DatePipe]
})
export class HackathonsModule { }
