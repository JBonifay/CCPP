import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {WorkspaceConfig, WorkspaceConfigRepository} from './workspace-config.repository';

@Injectable({providedIn: 'root'})
export class WorkspaceConfigApi implements WorkspaceConfigRepository {

  private http = inject(HttpClient);

  get(): Observable<WorkspaceConfig> {
    return this.http.get<WorkspaceConfig>('/api/workspace/config');
  }

  update(config: Partial<WorkspaceConfig>): Observable<WorkspaceConfig> {
    return this.http.patch<WorkspaceConfig>('/api/workspace/config', config);
  }
}
