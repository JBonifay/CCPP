import {Routes} from '@angular/router';
import {RouteTokens} from '../../app.routes';

export const HOME_ROUTES: Routes = [
  {
    path: RouteTokens.HOME,
    loadChildren: () =>
      import('./home.routes').then(m => m.HOME_ROUTES),
  }
];
