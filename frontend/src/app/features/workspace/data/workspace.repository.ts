import {Observable} from 'rxjs';
import {Workspace} from '../../../core';

export interface CreateWorkspaceRequest {
  name: string;
  logoUrl: string;
}

export abstract class WorkspaceRepository {
  abstract create(request: CreateWorkspaceRequest): Observable<Workspace>;
}
