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

    changeColor: rxMethod<{ id: string; color: string }>(
      pipe(
        tap(({id, color}) => {
          // Optimistic update
          const previousIdeas = store.ideas();
          const updatedIdeas = previousIdeas.map(idea =>
            idea.id === id ? {...idea, color} : idea
          );
          patchState(store, {ideas: updatedIdeas, error: null});
        }),
        switchMap(({id, color}) =>
          brainstormIdeaRepository.changeColor(id, color).pipe(
            catchError(() => {
              // Rollback on failure
              const previousIdeas = store.ideas(); // This assumes you saved previousIdeas in tap above
              patchState(store, {ideas: previousIdeas, error: 'Failed to change color'});
              return EMPTY;
            })
          )
        )
      )
    ),

    deleteIdea: rxMethod<string>(
      pipe(
        tap((ideaId: string) => {
          // Optimistic update
          const previousIdeas = store.ideas();
          const updatedIdeas = previousIdeas.filter(idea => idea.id !== ideaId);
          patchState(store, {ideas: updatedIdeas, error: null});
        }),
        switchMap((ideaId: string) =>
          brainstormIdeaRepository.deleteIdea(ideaId).pipe(
            catchError(() => {
              // Rollback on failure
              const previousIdeas = store.ideas(); // or save it in tap if needed
              patchState(store, {ideas: previousIdeas, error: 'Failed to delete idea'});
              return EMPTY;
            })
          )
        )
      )
    ),

    updateIdea: rxMethod<{ id: string; title: string; description: string }>(
      pipe(
        tap(({id, title, description}) => {
          const updatedIdeas = store.ideas().map(idea =>
            idea.id === id ? {...idea, title, description} : idea
          );
          patchState(store, {ideas: updatedIdeas, error: null});
        }),
        switchMap(({id, title, description}) =>
          brainstormIdeaRepository.updateIdea(id, title, description).pipe(
            catchError(() => {
              patchState(store, {error: 'Failed to update idea'});
              return EMPTY;
            })
          )
        )
      )
    ),

    clearError(): void {
      patchState(store, {error: null});
    },
  }))
);
