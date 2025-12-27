import {Component, inject} from '@angular/core';
import {AuthStore} from '../../core';
import {MenuItem, PrimeIcons} from 'primeng/api';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AppRoutePaths, RouteTokens} from '../../app.routes';

@Component({
  selector: 'app-layout',
  imports: [
    RouterOutlet,
    RouterLinkActive,
    RouterLink
  ],
  templateUrl: './app-layout.component.html',
  styleUrls: ['./app-layout.component.css'],
})
export class AppLayoutComponent {

  readonly authStore = inject(AuthStore);

  items: MenuItem[] = [
    {
      label: 'Navigation',
      items: [
        {
          icon: PrimeIcons.HOME,
          routerLink: AppRoutePaths.projects(),
          exact: true
        },
        {
          icon: PrimeIcons.FOLDER,
          routerLink: AppRoutePaths.project(1)
        }
      ]
    },
    {
      label: 'Management',
      items: [
        {
          icon: PrimeIcons.USERS,
          routerLink: ['/customers']
        },
        {
          icon: 'pi pi-chart-bar',
          routerLink: ['/reports']
        }
      ]
    },
  ];

  protected logout() {
    this.authStore.logout();
  }

  protected readonly PrimeIcons = PrimeIcons;
  protected readonly RouteTokens = RouteTokens;
}
