import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProjectsRepository} from './projects.repository';
import {ProjectListItem} from './model/project-list-item';

@Injectable({ providedIn: 'root' })
export class ProjectsApi implements ProjectsRepository {
  private http = inject(HttpClient);

  getAll(): Observable<ProjectListItem[]> {
    return this.http.get<ProjectListItem[]>('/api/projects');
  }

}
