import {patchState, signalStore, withMethods, withState} from '@ngrx/signals';
import {inject} from '@angular/core';
import {rxMethod} from '@ngrx/signals/rxjs-interop';
import {catchError, EMPTY, pipe, switchMap, tap} from 'rxjs';
import {UserListItem, UserRepository} from '../data/user.repository';
import {UserRole} from '../../../core';

export interface UserState {
  users: UserListItem[];
  loading: boolean;
  error: string | null;
}

const initialState: UserState = {
  users: [],
  loading: false,
  error: null,
};

export const UserStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),

  withMethods((store, repository = inject(UserRepository)) => ({
    loadUsers: rxMethod<void>(
      pipe(
        tap(() => patchState(store, {loading: true, error: null})),
        switchMap(() =>
          repository.getAll().pipe(
            tap({
              next: (users: UserListItem[]) =>
                patchState(store, {users, loading: false}),
              error: (err: any) =>
                patchState(store, {loading: false, error: err?.message ?? 'Failed to load users'}),
            }),
            catchError(() => EMPTY)
          )
        )
      )
    ),

    updateRole: rxMethod<{userId: string; role: UserRole}>(
      pipe(
        tap(({userId, role}) => {
          const updatedUsers = store.users().map(user =>
            user.id === userId ? {...user, role} : user
          );
          patchState(store, {users: updatedUsers, error: null});
        }),
        switchMap(({userId, role}) =>
          repository.updateRole(userId, role).pipe(
            catchError(() => {
              patchState(store, {error: 'Failed to update role'});
              return EMPTY;
            })
          )
        )
      )
    ),
  }))
);
