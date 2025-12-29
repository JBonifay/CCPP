import {environment} from '../../../environments/environment';
import {BrainstormIdeaRepository} from './data/brainstorm-idea.repository';
import {BrainstormIdeaMock} from './data/brainstorm-idea.mock';
import {BrainstormIdeaApi} from './data/brainstorm-idea.api';

export const brainstormProviders = [
  {
    provide: BrainstormIdeaRepository,
    useClass: environment.useMocks ? BrainstormIdeaMock : BrainstormIdeaApi,
  },
];
