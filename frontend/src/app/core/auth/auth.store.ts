import { computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { signalStore, withState, withComputed, withMethods, patchState } from '@ngrx/signals';

export interface User {
  id: string;
  email: string;
  name: string;
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
    userName: computed(() => store.user()?.name ?? ''),
    userInitial: computed(() => store.user()?.name?.charAt(0).toUpperCase() ?? 'U'),
  })),
  withMethods((store, router = inject(Router)) => ({
    initialize(): void {
      const stored = localStorage.getItem('user');
      if (stored) {
        patchState(store, { user: JSON.parse(stored) });
      }
    },

    async login(email: string, password: string): Promise<boolean> {
      patchState(store, { loading: true, error: null });

      try {
        // TODO: Replace with actual API call
        await new Promise((resolve) => setTimeout(resolve, 500));

        const user: User = {
          id: '1',
          email,
          name: email.split('@')[0],
        };

        localStorage.setItem('user', JSON.stringify(user));
        patchState(store, { user, loading: false });
        return true;
      } catch (error) {
        patchState(store, {
          loading: false,
          error: 'Login failed. Please try again.',
        });
        return false;
      }
    },

    logout(): void {
      localStorage.removeItem('user');
      patchState(store, { user: null, error: null });
      router.navigate(['/']);
    },

    clearError(): void {
      patchState(store, { error: null });
    },
  }))
);
