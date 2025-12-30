import {Routes} from '@angular/router';
import {workspaceProviders} from './workspace.providers';

export const WORKSPACE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./ui/select-workspace').then(m => m.SelectWorkspace),
  },
  {
    path: 'create',
    providers: workspaceProviders,
    loadComponent: () =>
      import('./ui/create-workspace').then(m => m.CreateWorkspace),
  },
];
