import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';

import {ButtonModule} from 'primeng/button';
import {CardModule} from 'primeng/card';
import {ProgressSpinnerModule} from 'primeng/progressspinner';

import {Fieldset} from 'primeng/fieldset';
import {ProjectListStore} from '../../store/project-list.store';
import {TableModule} from 'primeng/table';
import {Skeleton} from 'primeng/skeleton';

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
  private readonly store = inject(ProjectListStore);

  constructor() {
    this.store.loadProjects();
  }

  projects() {
    return this.store.projects();
  }

  isLoading() {
    return this.store.loading();
  }

  protected readonly Array = Array;

  protected onRowSelect($event: any) {
    this.store.selectProject($event.data.projectId);
  }
}
