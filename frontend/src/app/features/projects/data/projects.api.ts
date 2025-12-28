import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProjectsRepository} from './projects.repository';
import {ProjectListItem} from './model/project-list-item';
import {ProjectDetails} from "./model/project-details";

@Injectable({providedIn: 'root'})
export class ProjectsApi implements ProjectsRepository {

  private http = inject(HttpClient);

  getAll(): Observable<ProjectListItem[]> {
    return this.http.get<ProjectListItem[]>('/api/projects');
  }

  getById(id: string): Observable<ProjectDetails> {
    return this.http.get<ProjectDetails>(`/api/projects/${id}`);
  }

}
