import {Observable} from 'rxjs';
import {ProjectListItem} from './model/project-list-item';
import {ProjectDetails} from './model/project-details';

export abstract class ProjectsRepository {
  abstract getAll(): Observable<ProjectListItem[]>;
  abstract getById(id: string): Observable<ProjectDetails>;
}
