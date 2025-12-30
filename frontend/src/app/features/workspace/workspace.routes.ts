import {Routes} from '@angular/router';

export const WORKSPACE_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./ui/select-workspace').then(m => m.SelectWorkspace),
  },
];
