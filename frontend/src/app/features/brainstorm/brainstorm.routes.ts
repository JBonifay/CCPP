import {Routes} from '@angular/router';

export const BRAINSTORM_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./ui/brainstorm-component/brainstorm-component')
        .then(m => m.BrainstormComponent),
  }
];
