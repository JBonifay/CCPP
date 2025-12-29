import {Observable} from 'rxjs';

export interface WorkspaceConfig {
  name: string;
  logoUrl: string;
  fontFamily: string;
}

export abstract class WorkspaceConfigRepository {
  abstract get(): Observable<WorkspaceConfig>;
  abstract update(config: Partial<WorkspaceConfig>): Observable<WorkspaceConfig>;
}
