import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HackathonsComponent } from './components/hackathons/hackathons.component';
import { CreateHackathonComponent } from './components/create-hackathon/create-hackathon.component';
import { HackathonDetailsComponent } from './components/hackathon-details/hackathon-details.component';
import { MyHackathonComponent } from './components/my-hackathon/my-hackathon.component';
import { JudgeHackathonDashboardComponent } from './components/judge-hackathon-dashboard/judge-hackathon-dashboard.component';
import { MentorHackathonDashboardComponent } from './components/mentor-hackathon-dashboard/mentor-hackathon-dashboard.component';

const routes: Routes = [
  {
    path: 'create',
    component: CreateHackathonComponent
  },
  {
    path: 'my/:id',
    component: MyHackathonComponent
  },
  {
    path: 'judge/:id',
    component: JudgeHackathonDashboardComponent
  },
  {
    path: 'mentor/:id',
    component: MentorHackathonDashboardComponent
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
