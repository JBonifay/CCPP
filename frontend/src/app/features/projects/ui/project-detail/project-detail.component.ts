import {Component, inject} from '@angular/core';
import {ProjectDetailsStore} from '../../store/project-details.store';

@Component({
  selector: 'app-project-detail',
  imports: [],
  template: `
    {{ project() }}

  `,
})
export class ProjectDetailComponent {
  readonly projectDetailsStore = inject(ProjectDetailsStore);

  constructor() {
    this.projectDetailsStore.project();
  }

  protected project() {
    return this.projectDetailsStore.project();
  }

}
