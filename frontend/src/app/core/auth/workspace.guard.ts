import {inject} from '@angular/core';
import {Router, type CanActivateFn} from '@angular/router';
import {AuthStore} from './auth.store';

export const workspaceGuard: CanActivateFn = () => {
  const authStore = inject(AuthStore);
  const router = inject(Router);

  if (authStore.hasWorkspaceSelected()) {
    return true;
  }

  return router.createUrlTree(['/select-workspace']);
};

export const needsWorkspaceGuard: CanActivateFn = () => {
  const authStore = inject(AuthStore);
  const router = inject(Router);

  if (!authStore.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (authStore.hasWorkspaceSelected()) {
    return router.createUrlTree(['/app']);
  }

  return true;
};
