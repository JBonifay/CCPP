import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {ApiService, Workspace} from '../../../core';
import {CreateWorkspaceRequest, WorkspaceRepository} from './workspace.repository';

@Injectable({providedIn: 'root'})
export class WorkspaceApi implements WorkspaceRepository {
  private readonly http = inject(ApiService);

  create(request: CreateWorkspaceRequest): Observable<Workspace> {
    return this.http.post<Workspace>('/workspaces', request);
  }
}
