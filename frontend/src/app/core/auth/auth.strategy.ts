import {Observable} from 'rxjs';
import {User} from './user';
import {Workspace} from './workspace';

export const STORAGE_KEYS = {
  accessToken: 'token',
  refreshToken: 'refreshToken',
  user: 'user',
  selectedWorkspaceId: 'selectedWorkspaceId',
} as const;

export interface RestoreResult {
  user: User;
  selectedWorkspace: Workspace | null;
}

export abstract class AuthStrategy {
  abstract login(email: string, password: string): Observable<User>;
  abstract logout(): Observable<void>;
  abstract refreshUser(): Observable<User>;
  abstract selectWorkspace(workspaceId: string): Observable<Workspace>;
  abstract clearSelectedWorkspace(): void;
  abstract restore(): RestoreResult | null;
}
