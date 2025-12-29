import {Observable} from 'rxjs';
import {BrainstormIdea} from './model/brainstorm-idea';

export abstract class BrainstormIdeaRepository {
  abstract getAll(): Observable<BrainstormIdea[]>;
  abstract changeColor(ideaId: string, color: string): Observable<void>;
  abstract deleteIdea(ideaId: string): Observable<void>;
  abstract updateIdea(ideaId: string, title: string, description: string): Observable<void>;
}
