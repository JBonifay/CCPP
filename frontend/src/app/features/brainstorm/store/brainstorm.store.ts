import {BrainstormIdea} from '../data/model/brainstorm-idea';
import {patchState, signalStore, withComputed, withMethods, withState} from '@ngrx/signals';
import {computed, inject} from '@angular/core';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {catchError, EMPTY, pipe, switchMap, tap} from 'rxjs';
import {BrainstormIdeaRepository} from '../data/brainstorm-idea.repository';

export interface BrainstormState {
  ideas: BrainstormIdea[];
  loading: boolean;
  error: string | null;
}

const initialState: BrainstormState = {
  ideas: [],
  loading: false,
  error: null,
};

export const BrainstormStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withComputed(({ideas}) => ({
    ideasCount: computed(() => ideas.length),
  })),

  withMethods((store, brainstormIdeaRepository = inject(BrainstormIdeaRepository)) => ({
    loadIdeas: rxMethod<void>(
      pipe(
        tap(() => patchState(store, {loading: true, error: null})),
        switchMap(() =>
          brainstormIdeaRepository.getAll().pipe(
            tap({
              next: (ideas: BrainstormIdea[]) =>
                patchState(store, {ideas, loading: false}),
              error: (err: any) =>
                patchState(store, {loading: false, error: err?.message ?? 'Failed to load ideas'}),
            }),
            catchError(() => EMPTY)
          )
        )
      )
    ),

    changeColor(ideaId: string, color: string): void {
      const previousIdeas = store.ideas();
      const updatedIdeas = previousIdeas.map(idea =>
        idea.id === ideaId ? {...idea, color} : idea
      );
      patchState(store, {ideas: updatedIdeas});

      brainstormIdeaRepository.changeColor(ideaId, color).pipe(
        catchError(() => {
          patchState(store, {ideas: previousIdeas, error: 'Failed to change color'});
          return EMPTY;
        })
      ).subscribe();
    },

    clearError(): void {
      patchState(store, {error: null});
    },
  }))
);
