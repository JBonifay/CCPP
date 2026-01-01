import {Injectable} from '@angular/core';
import {delay, Observable, of, tap} from 'rxjs';
import {STORAGE_KEYS, User, Workspace} from '../../../core';
import {CreateWorkspaceRequest, WorkspaceRepository} from './workspace.repository';

@Injectable({providedIn: 'root'})
export class WorkspaceMock implements WorkspaceRepository {

  create(request: CreateWorkspaceRequest): Observable<Workspace> {
    const workspace: Workspace = {
      workspaceId: `ws-${Date.now()}`,
      workspaceName: request.name,
      workspaceLogoUrl: request.logoUrl,
      userRole: 'ADMIN'
    };
    return of(workspace).pipe(
      delay(300),
      tap(ws => this.addWorkspaceToStoredUser(ws))
    );
  }

  private addWorkspaceToStoredUser(workspace: Workspace): void {
    const storedUser = localStorage.getItem(STORAGE_KEYS.user);
    if (!storedUser) return;
    const user = JSON.parse(storedUser) as User;
    user.workspaces = [...user.workspaces, workspace];
    localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
  }
}
