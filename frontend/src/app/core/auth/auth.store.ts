import { computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { signalStore, withState, withComputed, withMethods, withHooks, patchState } from '@ngrx/signals';
import { AUTH_STRATEGY } from './auth.strategy';

export type UserRole = 'admin' | 'user';

export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
}

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
  withMethods((store, router = inject(Router), authStrategy = inject(AUTH_STRATEGY)) => ({
    async login(email: string, password: string): Promise<boolean> {
      patchState(store, { loading: true, error: null });

      const result = await authStrategy.login(email, password);

      if (result.success && result.user) {
        localStorage.setItem('user', JSON.stringify(result.user));
        if (result.token) {
          localStorage.setItem('token', result.token);
        }
        patchState(store, { user: result.user, loading: false });
        return true;
      } else {
        patchState(store, {
          loading: false,
          error: result.error ?? 'Login failed. Please try again.',
        });
        return false;
      }
    },

    async logout(): Promise<void> {
      await authStrategy.logout();
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      patchState(store, { user: null, error: null });
      await router.navigate(['/']);
    },

    clearError(): void {
      patchState(store, { error: null });
    },
  })),
  withHooks({
    onInit(store) {
      const storedUser = localStorage.getItem('user');
      const token = localStorage.getItem('token');
      if (storedUser && token) {
        patchState(store, { user: JSON.parse(storedUser) });
      }
    },
  })
);
