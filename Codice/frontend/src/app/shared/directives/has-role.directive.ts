import { Directive, Input, TemplateRef, ViewContainerRef, OnDestroy, OnChanges } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Subscription } from 'rxjs';

@Directive({
  selector: '[appHasRole]'
})
export class HasRoleDirective implements OnDestroy, OnChanges {
  @Input('appHasRole') allowedRoles: string[] = [];
  private subscription: Subscription;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: AuthService
  ) {
    this.subscription = this.authService.currentUser$.subscribe(() => {
      this.updateView();
    });
  }

  ngOnChanges(): void {
    this.updateView();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  private updateView(): void {
    const hasRequiredRole = this.authService.hasAnyRole(this.allowedRoles);
    
    // Pulisci sempre prima di ricreare
    this.viewContainer.clear();
    
    if (hasRequiredRole) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
