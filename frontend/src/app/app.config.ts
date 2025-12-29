import {ApplicationConfig, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {providePrimeNG} from 'primeng/config';
import Lara from '@primeuix/themes/lara';

import {routes} from './app.routes';
import {authInterceptor, provideAuth} from './core';
import {projectsProviders} from './features/projects/projects.providers';
import {brainstormProviders} from './features/brainstorm/brainstorm.providers';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([authInterceptor])),
    providePrimeNG({
      theme: {
        preset: Lara,
        options: {
          darkModeSelector: 'none',
          cssLayer: {
            name: 'primeng',
            order: 'theme, base, primeng'
          }
        }
      },
      ripple: true,
    }),
    provideAuth(),
    projectsProviders,
    brainstormProviders
  ],
};
