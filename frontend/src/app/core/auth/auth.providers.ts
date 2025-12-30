import {environment} from '../../../environments/environment';
import {AUTH_STRATEGY} from './auth.strategy';
import {FakeAuthStrategy} from './fake-auth.strategy';
import {RealAuthStrategy} from './real-auth.strategy';

export const authProviders = [
  {
    provide: AUTH_STRATEGY,
    useClass: environment.useMocks ? FakeAuthStrategy : RealAuthStrategy,
  },
];
