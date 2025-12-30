import {environment} from '../../../environments/environment';
import {AuthStrategy} from './auth.strategy';
import {FakeAuthStrategy} from './fake-auth.strategy';
import {RealAuthStrategy} from './real-auth.strategy';

export const authProviders = [
  {
    provide: AuthStrategy,
    useClass: environment.useMocks ? FakeAuthStrategy : RealAuthStrategy,
  },
];
