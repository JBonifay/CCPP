import {ProjectsRepository} from './data/projects.repository';
import {ProjectsApi} from './data/projects.api';
import {ProjectsMock} from './data/projects.mock';
import {environment} from '../../../environments/environment';

export const projectsProviders = [
  {
    provide: ProjectsRepository,
    useClass: environment.useMocks ? ProjectsMock : ProjectsApi,
  },
];
