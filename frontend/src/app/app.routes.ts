import {Routes} from '@angular/router';
import {authGuard, needsWorkspaceGuard, publicGuard, workspaceGuard} from './core';
import {AppLayoutComponent, PublicLayoutComponent} from './layouts';

export const AppRoutePaths = {
  landing: () => [RouteTokens.LANDING],
  selectWorkspace: () => ['/', RouteTokens.SELECT_WORKSPACE],
  createWorkspace: () => ['/', RouteTokens.SELECT_WORKSPACE, RouteTokens.CREATE],
  home: () => ['/', RouteTokens.APP],
  projects: () => ['/', RouteTokens.APP, RouteTokens.PROJECTS],
  project: (id: string | number) => ['/', RouteTokens.APP, RouteTokens.PROJECTS, id],
  settings: () => ['/', RouteTokens.APP, RouteTokens.SETTINGS],
  brainstorm: () => ['/', RouteTokens.APP, RouteTokens.BRAINSTORM],
};

export const RouteTokens = {
  LANDING: '',
  LOGIN: 'login',
  SELECT_WORKSPACE: 'select-workspace',
  CREATE: 'create',
  APP: 'app',
  HOME: 'home',
  PROJECTS: 'projects',
  PROJECT_ID: ':id',
  SETTINGS: 'settings',
  BRAINSTORM: 'brainstorm'
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
    path: RouteTokens.SELECT_WORKSPACE,
    canActivate: [needsWorkspaceGuard],
    loadChildren: () =>
      import('./features/workspace/workspace.routes').then(m => m.WORKSPACE_ROUTES),
  },
  {
    path: RouteTokens.APP,
    component: AppLayoutComponent,
    canActivate: [authGuard, workspaceGuard],
    children: [
      {
        path: '',
        loadChildren: () => import('./features/home/home.routes').then(m => m.HOME_ROUTES),
      },
      {
        path: RouteTokens.PROJECTS,
        loadChildren: () => import('./features/projects/projects.routes').then(m => m.PROJECTS_ROUTES),
      },
      {
        path: RouteTokens.BRAINSTORM,
        loadChildren: () => import('./features/brainstorm/brainstorm.routes').then(m => m.BRAINSTORM_ROUTES),
      },
      {
        path: RouteTokens.SETTINGS,
        loadChildren: () => import('./features/settings/settings.routes').then(m => m.SETTINGS_ROUTES),
      }
    ],
  },
  {path: '**', redirectTo: RouteTokens.APP}
];
