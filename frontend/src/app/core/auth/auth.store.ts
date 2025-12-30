import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withHooks, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { catchError, EMPTY, pipe, switchMap, tap } from 'rxjs';
import { AuthStrategy } from './auth.strategy';
import { User } from './user';

interface AuthState {
  user: User | null;
  loading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  loading: false,
  error: null,
};

export const AuthStore = signalStore(
  { providedIn: 'root' },

  withState(initialState),

  withComputed((store) => ({
    isAuthenticated: computed(() => store.user() !== null),
    isAdmin: computed(() => store.user()?.role === 'admin'),
    userName: computed(() => store.user()?.name ?? ''),
    userInitial: computed(() => store.user()?.name?.charAt(0).toUpperCase() ?? 'U'),
  })),

  withMethods((store, authStrategy = inject(AuthStrategy)) => ({
    login: rxMethod<{ email: string; password: string }>(
      pipe(
        tap(() => patchState(store, { loading: true, error: null })),
        switchMap(({ email, password }) =>
          authStrategy.login(email, password).pipe(
            tap((user) => patchState(store, { user, loading: false })),
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

    logout: rxMethod<void>(
      pipe(
        switchMap(() =>
          authStrategy.logout().pipe(
            tap(() => patchState(store, { user: null, error: null })),
            catchError(() => {
              patchState(store, { user: null, error: null });
              return EMPTY;
            })
          )
        )
      )
    ),

    clearError(): void {
      patchState(store, { error: null });
    },
  })),

  withHooks({
    onInit(store) {
      const authStrategy = inject(AuthStrategy);
      const user = authStrategy.restore();

      if (user) {
        patchState(store, { user });
      }
    },
  })
);
