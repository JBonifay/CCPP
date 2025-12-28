import {patchState, signalStore, withComputed, withMethods, withState} from '@ngrx/signals';
import {computed, inject} from '@angular/core';
import {ProjectsRepository} from '../data/projects.repository';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {catchError, EMPTY, pipe, switchMap, tap} from 'rxjs';
import {ProjectListItem} from '../data/model/project-list-item';

export interface ProjectState {
  projects: ProjectListItem[];
  selectedProject: ProjectListItem | null;
  loading: boolean;
  error: string | null;
}

const initialState: ProjectState = {
  projects: [],
  selectedProject: null,
  loading: false,
  error: null,
};

export const ProjectsStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withComputed(({projects}) => ({
    projectsCount: computed(() => projects().length),
  })),

  withMethods((store, projectsRepository = inject(ProjectsRepository)) => ({
    loadProjects: rxMethod<void>(
      pipe(
        tap(() => patchState(store, {loading: true, error: null})),
        switchMap(() =>
          projectsRepository.getAll().pipe(
            tap({
              next: (projects: ProjectListItem[]) =>
                patchState(store, {projects, loading: false}),
              error: (err: any) =>
                patchState(store, {loading: false, error: err?.message ?? 'Failed to load projects'}),
            }),
            catchError(() => EMPTY)
          )
        )
      )
    ),

    selectProject(project: ProjectListItem | null): void {
      patchState(store, {selectedProject: project});
    },

    clearError(): void {
      patchState(store, {error: null});
    },
  }))
);
