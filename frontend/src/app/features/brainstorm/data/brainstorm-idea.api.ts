import {inject, Injectable} from '@angular/core';
import {BrainstormIdeaRepository} from './brainstorm-idea.repository';
import {Observable} from "rxjs";
import {BrainstormIdea} from "./model/brainstorm-idea";
import {HttpClient} from "@angular/common/http";

@Injectable({providedIn: 'root'})
export class BrainstormIdeaApi implements BrainstormIdeaRepository {

  private http = inject(HttpClient);

  getAll(): Observable<BrainstormIdea[]> {
    throw new Error("Method not implemented.");
  }

  createIdea(idea: Omit<BrainstormIdea, 'position'>): Observable<BrainstormIdea> {
    return this.http.post<BrainstormIdea>(`/brainstorm/ideas`, idea);
  }

  changeColor(ideaId: string, color: string): Observable<void> {
    return this.http.patch<void>(`/brainstorm/ideas/${ideaId}/color`, {color});
  }

  deleteIdea(ideaId: string): Observable<void> {
    return this.http.delete<void>(`/brainstorm/ideas/${ideaId}`);
  }

  updateIdea(ideaId: string, title: string, description: string): Observable<void> {
    return this.http.patch<void>(`/brainstorm/ideas/${ideaId}`, {title, description});
  }

}

