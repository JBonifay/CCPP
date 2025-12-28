import {Routes} from '@angular/router';
import {authGuard, publicGuard} from './core';
import {AppLayoutComponent, PublicLayoutComponent} from './layouts';

export const AppRoutePaths = {
  home: () => ['/', RouteTokens.APP],
  projects: () => ['/', RouteTokens.APP, RouteTokens.PROJECTS],
  project: (id: string | number) => ['/', RouteTokens.APP, RouteTokens.PROJECTS, id,],
  settings: () => ['/', RouteTokens.APP, RouteTokens.SETTINGS],
};

export const RouteTokens = {
  APP: 'app',
  PROJECTS: 'projects',
  HOME: 'home',
  PROJECT_ID: ':id',
  LOGIN: 'login',
  SETTINGS: 'settings'
} as const;

export const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    canActivate: [publicGuard],
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./features/landing/landing.routes').then(m => m.LANDING_ROUTES),
      },
      {
        path: RouteTokens.LOGIN,
        loadChildren: () =>
          import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES),
      },
    ],
  },
  {
    path: RouteTokens.APP,
    component: AppLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./features/home/home.routes').then(m => m.HOME_ROUTES),
      },
      {
        path: RouteTokens.PROJECTS,
        loadChildren: () =>
          import('./features/projects/projects.routes').then(m => m.PROJECTS_ROUTES),
      },
    ],
  },
  {path: '**', redirectTo: RouteTokens.APP}
];
