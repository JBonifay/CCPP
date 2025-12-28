import { Routes } from '@angular/router';
import {RouteTokens} from '../../app.routes';

export const PROJECTS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./ui/project-list/project-list.component')
        .then(m => m.ProjectListComponent),
  },
  {
    path: RouteTokens.PROJECT_ID,
    loadComponent: () =>
      import('./ui/project-detail/project-detail.component')
        .then(m => m.ProjectDetailComponent),
  },
];
