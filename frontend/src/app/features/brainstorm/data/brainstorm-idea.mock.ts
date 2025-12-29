import {Injectable} from '@angular/core';
import {BrainstormIdeaRepository} from './brainstorm-idea.repository';
import {Observable, of} from 'rxjs';
import {BrainstormIdea} from './model/brainstorm-idea';

@Injectable({providedIn: 'root'})
export class BrainstormIdeaMock implements BrainstormIdeaRepository {

  brainstormIdea: BrainstormIdea[] = [
    {
      id: 'b87d868b-8662-4855-b5bf-4a0fcd280948',
      title: 'User Onboarding Flow',
      description: 'Design a clearer onboarding flow with fewer steps, inline tips, and a short product tour to help users reach their first success faster.',
      color: "#78e770",
      position: {x: 20, y: 100}
    },
    {
      id: '7ea2e876-0b82-4824-99da-12a74e717e01',
      title: 'Real-Time Collaboration',
      description: 'Allow multiple users to edit and move cards simultaneously with live cursors and presence indicators.',
      color: "#e77070",
      position: {x: 200, y: 100}
    }
  ];

  getAll(): Observable<BrainstormIdea[]> {
    return of(this.brainstormIdea);
  }

  createIdea(idea: Omit<BrainstormIdea, 'position'>): Observable<BrainstormIdea> {
    console.log(`Creating idea: ${idea.title}`);
    const newIdea: BrainstormIdea = {...idea, position: {x: 100, y: 100}};
    this.brainstormIdea.push(newIdea);
    return of(newIdea);
  }

  changeColor(ideaId: string, color: string): Observable<void> {
    console.log(`Changing color of idea ${ideaId} to ${color}`);
    const idea = this.brainstormIdea.find(i => i.id === ideaId);
    if (idea) {
      idea.color = color;
    }
    return of(undefined);
  }

  deleteIdea(ideaId: string): Observable<void> {
    console.log(`Deleting idea ${ideaId}`);
    this.brainstormIdea = this.brainstormIdea.filter(i => i.id !== ideaId);
    return of(undefined);
  }

  updateIdea(ideaId: string, title: string, description: string): Observable<void> {
    console.log(`Updating idea ${ideaId}: ${title}`);
    const idea = this.brainstormIdea.find(i => i.id === ideaId);
    if (idea) {
      idea.title = title;
      idea.description = description;
    }
    return of(undefined);
  }

}

