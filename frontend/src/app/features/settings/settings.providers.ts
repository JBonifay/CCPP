import {WorkspaceConfigRepository} from './data/workspace-config.repository';
import {WorkspaceConfigMock} from './data/workspace-config.mock';
import {WorkspaceConfigApi} from './data/workspace-config.api';
import {UserRepository} from './data/user.repository';
import {UserMock} from './data/user.mock';
import {UserApi} from './data/user.api';
import {environment} from '../../../environments/environment';

export const settingsProviders = [
  {
    provide: WorkspaceConfigRepository,
    useClass: environment.useMocks ? WorkspaceConfigMock : WorkspaceConfigApi,
  },
  {
    provide: UserRepository,
    useClass: environment.useMocks ? UserMock : UserApi,
  },
];
