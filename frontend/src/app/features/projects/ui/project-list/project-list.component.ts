import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';

import {ButtonModule} from 'primeng/button';
import {CardModule} from 'primeng/card';
import {ProgressSpinnerModule} from 'primeng/progressspinner';

import {Fieldset} from 'primeng/fieldset';
import {ProjectsListStore} from '../../store/projects-list.store';
import {TableModule} from 'primeng/table';
import {Skeleton} from 'primeng/skeleton';
import {AppRoutePaths} from '../../../../app.routes';
import {VisitorModeService} from '../../../../shared/services/visitor-mode.service';

@Component({
  selector: 'app-project-list',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    CardModule,
    ProgressSpinnerModule,
    Fieldset,
    TableModule,
    Skeleton,
  ],
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.css']
})
export class ProjectListComponent {
  private readonly projectsListStore = inject(ProjectsListStore);
  protected readonly visitorMode = inject(VisitorModeService);
  private readonly router = inject(Router);

  constructor() {
    this.projectsListStore.loadProjects();
  }

  projects() {
    return this.projectsListStore.projects();
  }

  isLoading() {
    return this.projectsListStore.loading();
  }

  protected readonly Array = Array;

  onRowSelect(event: any) {
    const projectId = event.data.projectId;
    if (!projectId) return;
    this.router.navigate(AppRoutePaths.project(projectId));
  }
}
