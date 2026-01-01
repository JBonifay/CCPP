import {Injectable} from '@angular/core';
import {delay, finalize, Observable, of, tap} from 'rxjs';
import {AuthStrategy, RestoreResult, STORAGE_KEYS} from './auth.strategy';
import {User} from './user';
import {Workspace} from './workspace';

const FAKE_WORKSPACES: Workspace[] = [
  {workspaceId: 'ws-1', workspaceName: 'Personal', workspaceLogoUrl: '', userRole: 'ADMIN'},
  {workspaceId: 'ws-2', workspaceName: 'Acme Corp', workspaceLogoUrl: '/acme-logo.png', userRole: 'USER'},
  {workspaceId: 'ws-3', workspaceName: 'Startup Inc', workspaceLogoUrl: '', userRole: 'USER'},
];

@Injectable({providedIn: 'root'})
export class FakeAuthStrategy extends AuthStrategy {

  login(email: string, password: string): Observable<User> {
    this.clearStorage();

    const user: User = {
      id: 'fake-user-id',
      email,
      name: email.split('@')[0],
      workspaces: FAKE_WORKSPACES,
    };

    return of(user).pipe(
      delay(500),
      tap(u => {
        this.persistTokens();
        this.persistUser(u);
      })
    );
  }

  refreshUser(): Observable<User> {
    return this.login('', '');
  }

  selectWorkspace(workspaceId: string): Observable<Workspace> {
    const storedUser = localStorage.getItem(STORAGE_KEYS.user);
    if (!storedUser) {
      throw new Error('No user in storage');
    }

    const user = JSON.parse(storedUser) as User;
    const workspace = user.workspaces.find(w => w.workspaceId === workspaceId);
    if (!workspace) {
      throw new Error('Workspace not found');
    }

    return of(workspace).pipe(
      delay(300),
      tap(() => {
        this.persistTokens();
        this.persistSelectedWorkspace(workspaceId);
      })
    );
  }

  logout(): Observable<void> {
    return of(void 0).pipe(
      delay(100),
      finalize(() => this.clearStorage())
    );
  }

  clearSelectedWorkspace(): void {
    localStorage.removeItem(STORAGE_KEYS.selectedWorkspaceId);
  }

  override restore(): RestoreResult | null {
    const stored = localStorage.getItem(STORAGE_KEYS.user);
    if (!stored) {
      return null;
    }

    try {
      const user = JSON.parse(stored) as User;
      const selectedWorkspaceId = localStorage.getItem(STORAGE_KEYS.selectedWorkspaceId);

      let selectedWorkspace: Workspace | null = null;
      if (selectedWorkspaceId) {
        selectedWorkspace = user.workspaces.find(w => w.workspaceId === selectedWorkspaceId) ?? null;
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

  private persistTokens(): void {
    localStorage.setItem(STORAGE_KEYS.accessToken, 'fake-jwt-token');
    localStorage.setItem(STORAGE_KEYS.refreshToken, 'fake-refresh-token');
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
