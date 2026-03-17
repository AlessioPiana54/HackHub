import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InvitationsComponent } from './components/invitations/invitations.component';
import { InvitationsRoutingModule } from './invitations-routing.module';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [
    InvitationsComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    InvitationsRoutingModule
  ]
})
export class InvitationsModule { }
