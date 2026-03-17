import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FooterComponent } from './components/footer/footer.component';
import { HasRoleDirective } from './directives/has-role.directive';

@NgModule({
  declarations: [
    FooterComponent,
    HasRoleDirective
  ],
  imports: [
    CommonModule
  ],
  exports: [
    FooterComponent,
    HasRoleDirective
  ]
})
export class SharedModule { }
