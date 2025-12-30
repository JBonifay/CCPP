import {ApplicationConfig, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {providePrimeNG} from 'primeng/config';

import {routes} from './app.routes';
import {authInterceptor, authProviders} from './core';
import {projectsProviders} from './features/projects/projects.providers';
import {brainstormProviders} from './features/brainstorm/brainstorm.providers';
import {customerPreset} from './color-preset';
import {settingsProviders} from './features/settings/settings.providers';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([authInterceptor])),
    providePrimeNG({
      theme: {
        preset: customerPreset,
        options: {
          darkModeSelector: 'none',
        }
      },
      ripple: true,
    }),
    authProviders,
    projectsProviders,
    brainstormProviders,
    settingsProviders
  ],
};
