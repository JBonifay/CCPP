import {Injectable} from '@angular/core';
import {BrainstormIdeaRepository} from './brainstorm-idea.repository';
import {Observable} from "rxjs";
import {BrainstormIdea} from "./model/brainstorm-idea";

@Injectable({providedIn: 'root'})
export class BrainstormIdeaApi implements BrainstormIdeaRepository {

  getAll(): Observable<BrainstormIdea[]> {
        throw new Error("Method not implemented.");
    }

}

