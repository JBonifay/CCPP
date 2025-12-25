import { Provider } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AUTH_STRATEGY } from './auth.strategy';
import { FakeAuthStrategy } from './fake-auth.strategy';
import { RealAuthStrategy } from './real-auth.strategy';

export function provideAuth(): Provider[] {
  return [
    {
      provide: AUTH_STRATEGY,
      useClass: environment.useFakeAuth ? FakeAuthStrategy : RealAuthStrategy,
    },
  ];
}
