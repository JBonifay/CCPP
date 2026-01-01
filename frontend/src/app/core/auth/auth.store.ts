import {computed, inject} from '@angular/core';
import {patchState, signalStore, withComputed, withHooks, withMethods, withState} from '@ngrx/signals';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {catchError, EMPTY, pipe, switchMap, tap} from 'rxjs';
import {AuthStrategy} from './auth.strategy';
import {User} from './user';
import {Workspace} from './workspace';

interface AuthState {
  user: User | null;
  selectedWorkspace: Workspace | null;
  loading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  selectedWorkspace: null,
  loading: false,
  error: null,
};

export const AuthStore = signalStore(
  {providedIn: 'root'},

  withState(initialState),

  withComputed((store) => ({
    isAuthenticated: computed(() => store.user() !== null),
    hasWorkspaceSelected: computed(() => store.selectedWorkspace() !== null),
    isAdmin: computed(() => store.selectedWorkspace()?.userRole === 'ADMIN'),
    userName: computed(() => store.user()?.name ?? ''),
    workspaces: computed(() => store.user()?.workspaces ?? []),
    workspaceName: computed(() => store.selectedWorkspace()?.workspaceName ?? ''),
  })),

  withMethods((store, authStrategy = inject(AuthStrategy)) => ({
    login: rxMethod<{email: string; password: string}>(
      pipe(
        tap(() => patchState(store, {loading: true, error: null})),
        switchMap(({email, password}) =>
          authStrategy.login(email, password).pipe(
            tap((user) => patchState(store, {user, selectedWorkspace: null, loading: false})),
            catchError((error) => {
              patchState(store, {
                loading: false,
                error: error?.message ?? 'Login failed. Please try again.',
              });
              return EMPTY;
            })
          )
        )
      )
    ),

    // Observable for chaining (for select-workspace with navigation)
    selectWorkspace$(workspaceId: string) {
      patchState(store, {loading: true, error: null});
      return authStrategy.selectWorkspace(workspaceId).pipe(
        tap((workspace) => patchState(store, {selectedWorkspace: workspace, loading: false})),
        catchError((error) => {
          patchState(store, {loading: false, error: error?.message ?? 'Failed to select workspace.'});
          return EMPTY;
        })
      );
    },

    logout: rxMethod<void>(
      pipe(
        switchMap(() =>
          authStrategy.logout().pipe(
            tap(() => patchState(store, {user: null, selectedWorkspace: null, error: null})),
            catchError(() => {
              patchState(store, {user: null, selectedWorkspace: null, error: null});
              return EMPTY;
            })
          )
        )
      )
    ),

    clearSelectedWorkspace(): void {
      authStrategy.clearSelectedWorkspace();
      patchState(store, {selectedWorkspace: null});
    },

    clearError(): void {
      patchState(store, {error: null});
    },

    refreshUser: rxMethod<void>(
      pipe(
        tap(() => patchState(store, {loading: true, error: null})),
        switchMap(() =>
          authStrategy.refreshUser().pipe(
            tap((user) => patchState(store, {user: user, loading: false})),
            catchError((error) => {
              patchState(store, {loading: false, error: error?.message ?? 'Failed to refresh.'});
              return EMPTY;
            })
          )
        )
      )
    ),

  })),

  withHooks({
    onInit(store) {
      const authStrategy = inject(AuthStrategy);
      const result = authStrategy.restore();

      if (result) {
        patchState(store, {
          user: result.user,
          selectedWorkspace: result.selectedWorkspace,
        });
      }
    },
  })
);
