import {ProjectDetails} from '../data/model/project-details';
import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {inject} from '@angular/core';
import {ProjectsRepository} from '../data/projects.repository';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {catchError, EMPTY, pipe, switchMap, tap} from 'rxjs';

export interface ProjectState {
  project: ProjectDetails | null;
  loading: boolean;
  error: string | null;
}

const initialState: ProjectState = {
  project: null,
  loading: false,
  error: null,
};

export const ProjectDetailsStore = signalStore(
  {providedIn: 'root'},

  withState(initialState),

  // withComputed(),

  withMethods((store, projectsRepository = inject(ProjectsRepository)) => ({
    loadProject: rxMethod<string>(
      pipe(
        tap(() => patchState(store, {loading: true, error: null})),
        switchMap((projectId) =>  // Capture the projectId parameter here
          projectsRepository.getById(projectId).pipe(  // Pass it to getById
            tap({
              next: (project: ProjectDetails) =>
                patchState(store, {project, loading: false}),
              error: (err: any) =>
                patchState(store, {loading: false, error: err?.message ?? 'Failed to load project'}),
            }),
            catchError(() => EMPTY)
          )
        )
      )
    ),

    clearProject: () => {
      patchState(store, {project: null, error: null, loading: false});
    }
  }))
);
