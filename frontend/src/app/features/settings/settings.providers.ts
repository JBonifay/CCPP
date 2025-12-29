import {WorkspaceConfigRepository} from './data/workspace-config.repository';
import {WorkspaceConfigMock} from './data/workspace-config.mock';
import {WorkspaceConfigApi} from './data/workspace-config.api';
import {environment} from '../../../environments/environment';

export const settingsProviders = [
  {
    provide: WorkspaceConfigRepository,
    useClass: environment.useMocks ? WorkspaceConfigMock : WorkspaceConfigApi,
  },
];
