import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {WorkspaceConfig, WorkspaceConfigRepository} from './workspace-config.repository';

@Injectable({providedIn: 'root'})
export class WorkspaceConfigMock implements WorkspaceConfigRepository {

  private config: WorkspaceConfig = {
    name: 'Creator',
    logoUrl: '/creator_logo.png',
    fontFamily: 'Rubik'
  };

  get(): Observable<WorkspaceConfig> {
    return of(this.config);
  }

  update(partial: Partial<WorkspaceConfig>): Observable<WorkspaceConfig> {
    this.config = {...this.config, ...partial};
    return of(this.config);
  }
}
