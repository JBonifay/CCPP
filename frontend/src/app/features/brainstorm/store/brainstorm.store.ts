import {BrainstormIdea} from '../data/model/brainstorm-idea';
import {patchState, signalStore, withComputed, withMethods, withState} from '@ngrx/signals';
import {computed, inject} from '@angular/core';
import {ProjectsRepository} from '../../projects/data/projects.repository';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {catchError, EMPTY, pipe, switchMap, tap} from 'rxjs';
import {ProjectListItem} from '../../projects/data/model/project-list-item';
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

    clearError(): void {
      patchState(store, {error: null});
    },
  }))
);
