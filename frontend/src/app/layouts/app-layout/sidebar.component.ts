import {AppRoutePaths, RouteTokens} from '../../app.routes';
import {MenuItem, PrimeIcons} from 'primeng/api';
import {AuthStore} from '../../core';
import {Component, inject} from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  imports: [
    RouterLink,
    RouterLinkActive
  ],
})
export class SidebarComponent {

  readonly authStore = inject(AuthStore);

  items: MenuItem[] = [
    {
      label: 'Navigation',
      items: [
        {
          icon: PrimeIcons.HOME,
          routerLink: AppRoutePaths.home(),
          exact: true
        },
        {
          icon: PrimeIcons.FOLDER,
          routerLink: AppRoutePaths.projects()
        },
        {
          icon: PrimeIcons.LIGHTBULB,
          routerLink: AppRoutePaths.brainstorm()
        }
      ]
    },
  ];

  protected logout() {
    this.authStore.logout();
  }

  protected readonly RouteTokens = RouteTokens;
  protected readonly PrimeIcons = PrimeIcons;
}
