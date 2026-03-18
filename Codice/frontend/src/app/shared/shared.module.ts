import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FooterComponent } from './components/footer/footer.component';
import { ToastComponent } from './components/toast/toast.component';
import { HasRoleDirective } from './directives/has-role.directive';

@NgModule({
  declarations: [
    FooterComponent,
    ToastComponent,
    HasRoleDirective
  ],
  imports: [
    CommonModule
  ],
  exports: [
    FooterComponent,
    ToastComponent,
    HasRoleDirective
  ]
})
export class SharedModule { }
