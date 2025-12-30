import {environment} from '../../../environments/environment';
import {WorkspaceRepository} from './data/workspace.repository';
import {WorkspaceMock} from './data/workspace.mock';
import {WorkspaceApi} from './data/workspace.api';

export const workspaceProviders = [
  {
    provide: WorkspaceRepository,
    useClass: environment.useMocks ? WorkspaceMock : WorkspaceApi,
  },
];
