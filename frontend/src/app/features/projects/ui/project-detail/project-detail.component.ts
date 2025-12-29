import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {ProjectDetailsStore} from '../../store/project-details.store';
import {DatePipe} from '@angular/common';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-project-detail',
  imports: [DatePipe],
  templateUrl: './project-detail.component.html',
})
export class ProjectDetailComponent implements OnInit, OnDestroy {
  readonly projectDetailsStore = inject(ProjectDetailsStore);
  private route = inject(ActivatedRoute);

  ngOnInit() {
    const projectId = this.route.snapshot.paramMap.get('id');
    if (projectId) {
      this.projectDetailsStore.loadProject(projectId);
    }
  }

  ngOnDestroy() {
    this.projectDetailsStore.clearProject();
  }

  protected project() {
    return this.projectDetailsStore.project();
  }

  protected isLoading() {
    return this.projectDetailsStore.loading();
  }

  protected error() {
    return this.projectDetailsStore.error();
  }

}
