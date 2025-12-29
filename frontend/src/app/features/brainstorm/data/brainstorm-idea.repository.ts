import {Observable} from 'rxjs';
import {BrainstormIdea} from './model/brainstorm-idea';

export abstract class BrainstormIdeaRepository {
  abstract getAll(): Observable<BrainstormIdea[]>;
  abstract changeColor(ideaId: string, color: string): Observable<void>;
}
