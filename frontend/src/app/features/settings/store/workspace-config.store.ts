import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {inject} from '@angular/core';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {catchError, EMPTY, pipe, switchMap, tap} from 'rxjs';
import {WorkspaceConfig, WorkspaceConfigRepository} from '../data/workspace-config.repository';

export interface WorkspaceConfigState {
  config: WorkspaceConfig;
  loading: boolean;
  error: string | null;
}

const DEFAULT_CONFIG: WorkspaceConfig = {
  name: 'Creator',
  logoUrl: '/creator_logo.png',
  fontFamily: 'Rubik'
};

const initialState: WorkspaceConfigState = {
  config: DEFAULT_CONFIG,
  loading: false,
  error: null,
};

export const WorkspaceConfigStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withMethods((store, repository = inject(WorkspaceConfigRepository)) => ({
    loadConfig: rxMethod<void>(
      pipe(
        tap(() => patchState(store, {loading: true, error: null})),
        switchMap(() =>
          repository.get().pipe(
            tap({
              next: (config: WorkspaceConfig) => {
                patchState(store, {config, loading: false});
                applyFont(config.fontFamily);
              },
              error: (err: any) =>
                patchState(store, {loading: false, error: err?.message ?? 'Failed to load config'}),
            }),
            catchError(() => EMPTY)
          )
        )
      )
    ),

    updateConfig: rxMethod<Partial<WorkspaceConfig>>(
      pipe(
        tap((partial) => {
          const updatedConfig = {...store.config(), ...partial};
          patchState(store, {config: updatedConfig, error: null});
          if (partial.fontFamily) {
            applyFont(partial.fontFamily);
          }
        }),
        switchMap((partial) =>
          repository.update(partial).pipe(
            catchError(() => {
              patchState(store, {error: 'Failed to update config'});
              return EMPTY;
            })
          )
        )
      )
    ),
  }))
);

function applyFont(fontFamily: string): void {
  document.body.style.fontFamily = `"${fontFamily}", sans-serif`;
}
