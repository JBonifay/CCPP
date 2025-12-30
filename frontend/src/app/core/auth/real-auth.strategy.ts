import {inject, Injectable} from '@angular/core';
import {finalize, map, Observable, switchMap, tap} from 'rxjs';
import {ApiService} from '../api/api.service';
import {AuthStrategy, RestoreResult, STORAGE_KEYS} from './auth.strategy';
import {User} from './user';
import {Workspace} from './workspace';

interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

@Injectable({providedIn: 'root'})
export class RealAuthStrategy extends AuthStrategy {
  private readonly api = inject(ApiService);

  login(email: string, password: string): Observable<User> {
    this.clearStorage();
    return this.api.post<AuthTokens>('/auth/login', {email, password}).pipe(
      tap(tokens => this.persistTokens(tokens)),
      switchMap(() =>
        this.api.get<User>('/auth/me').pipe(
          tap(user => this.persistUser(user))
        )
      )
    );
  }

  selectWorkspace(workspaceId: string): Observable<Workspace> {
    const refreshToken = localStorage.getItem(STORAGE_KEYS.refreshToken);
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    return this.api.post<AuthTokens>('/auth/select-workspace', {
      refreshToken,
      workspaceId,
    }).pipe(
      tap(tokens => this.persistTokens(tokens)),
      map(() => {
        const storedUser = localStorage.getItem(STORAGE_KEYS.user);
        if (!storedUser) {
          throw new Error('No user in storage');
        }
        const user = JSON.parse(storedUser) as User;
        const workspace = user.workspaces.find(w => w.id === workspaceId);
        if (!workspace) {
          throw new Error('Workspace not found');
        }
        this.persistSelectedWorkspace(workspaceId);
        return workspace;
      })
    );
  }

  logout(): Observable<void> {
    return this.api.post<void>('/auth/logout', {}).pipe(
      finalize(() => this.clearStorage())
    );
  }

  clearSelectedWorkspace(): void {
    localStorage.removeItem(STORAGE_KEYS.selectedWorkspaceId);
  }

  override restore(): RestoreResult | null {
    const token = localStorage.getItem(STORAGE_KEYS.accessToken);
    const storedUser = localStorage.getItem(STORAGE_KEYS.user);
    const selectedWorkspaceId = localStorage.getItem(STORAGE_KEYS.selectedWorkspaceId);

    if (!token || !storedUser) {
      return null;
    }

    try {
      const user = JSON.parse(storedUser) as User;

      let selectedWorkspace: Workspace | null = null;
      if (selectedWorkspaceId) {
        selectedWorkspace = user.workspaces.find(w => w.id === selectedWorkspaceId) ?? null;
        if (!selectedWorkspace) {
          localStorage.removeItem(STORAGE_KEYS.selectedWorkspaceId);
        }
      }

      return {user, selectedWorkspace};
    } catch {
      this.clearStorage();
      return null;
    }
  }

  private persistTokens(tokens: AuthTokens): void {
    localStorage.setItem(STORAGE_KEYS.accessToken, tokens.accessToken);
    localStorage.setItem(STORAGE_KEYS.refreshToken, tokens.refreshToken);
  }

  private persistUser(user: User): void {
    localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
  }

  private persistSelectedWorkspace(workspaceId: string): void {
    localStorage.setItem(STORAGE_KEYS.selectedWorkspaceId, workspaceId);
  }

  private clearStorage(): void {
    localStorage.removeItem(STORAGE_KEYS.accessToken);
    localStorage.removeItem(STORAGE_KEYS.refreshToken);
    localStorage.removeItem(STORAGE_KEYS.user);
    localStorage.removeItem(STORAGE_KEYS.selectedWorkspaceId);
  }
}
