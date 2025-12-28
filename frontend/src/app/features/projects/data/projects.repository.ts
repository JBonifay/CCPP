import {Observable} from 'rxjs';
import {ProjectListItem} from './model/project-list-item';

export abstract class ProjectsRepository {
  abstract getAll(): Observable<ProjectListItem[]>;
}
