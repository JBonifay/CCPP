import { Component, effect, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { MenuItem, PrimeIcons } from 'primeng/api';
import { AppRoutePaths, RouteTokens } from '../../app.routes';
import { AuthStore } from '../../core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  imports: [RouterLink, RouterLinkActive],
})
export class SidebarComponent {
  private readonly router = inject(Router);
  readonly authStore = inject(AuthStore);

  items: MenuItem[] = [
    {
      label: 'Navigation',
      items: [
        {
          icon: PrimeIcons.HOME,
          routerLink: AppRoutePaths.home(),
          exact: true,
        },
        {
          icon: PrimeIcons.FOLDER,
          routerLink: AppRoutePaths.projects(),
        },
        {
          icon: PrimeIcons.LIGHTBULB,
          routerLink: AppRoutePaths.brainstorm(),
        },
      ],
    },
  ];

  private loggedOut = false;

  constructor() {
    effect(() => {
      const isAuthenticated = this.authStore.isAuthenticated();
      if (!isAuthenticated && this.loggedOut) {
        this.router.navigate([AppRoutePaths.landing()]);
        this.loggedOut = false;
      }
    });
  }

  protected logout() {
    this.loggedOut = true;
    this.authStore.logout();
  }

  protected switchWorkspace() {
    this.authStore.clearSelectedWorkspace();
  }

  protected readonly RouteTokens = RouteTokens;
  protected readonly PrimeIcons = PrimeIcons;
  protected readonly AppRoutePaths = AppRoutePaths;
}
