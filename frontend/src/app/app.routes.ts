import {Routes} from '@angular/router';
import {authGuard, publicGuard} from './core';
import {AppLayoutComponent, PublicLayoutComponent} from './layouts';

export const AppRoutePaths = {
  settings: () => ['/', RouteTokens.APP, RouteTokens.SETTINGS],
  projects: () => ['/', RouteTokens.APP, RouteTokens.PROJECTS],
  project: (id: string | number) => [
    '/',
    RouteTokens.APP,
    RouteTokens.PROJECTS,
    id,
  ],
};

export const RouteTokens = {
  APP: 'app',
  PROJECTS: 'projects',
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
          import('./features/home/home.routes').then(m => m.HOME_ROUTES),
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
        redirectTo: RouteTokens.PROJECTS,
        pathMatch: 'full',
      },
      {
        path: RouteTokens.PROJECTS,
        loadChildren: () =>
          import('./features/projects/projects.routes').then(m => m.PROJECTS_ROUTES),
      },
    ],
  },
  {path: '**', redirectTo: ''},
];
